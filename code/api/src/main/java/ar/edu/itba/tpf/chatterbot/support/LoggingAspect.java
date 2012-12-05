package ar.edu.itba.tpf.chatterbot.support;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

import ar.edu.itba.tpf.chatterbot.dao.ErrorLogDAO;
import ar.edu.itba.tpf.chatterbot.exception.ChatterbotException;
import ar.edu.itba.tpf.chatterbot.exception.ChatterbotServiceException;
import ar.edu.itba.tpf.chatterbot.model.ErrorLog;

@Aspect
public class LoggingAspect {
    private ErrorLogDAO errorLogDAO;
    private static final Logger logger = Logger.getLogger(LoggingAspect.class);

    public void setErrorLogDAO(ErrorLogDAO errorLogDAO) {
        this.errorLogDAO = errorLogDAO;
    }

    @Around("execution(* ar.edu.itba.tpf.chatterbot.service.*.*(..))")
    public Object doRecoveryActions(ProceedingJoinPoint pjp) throws Throwable {
        Object retVal = null;
        try {
            retVal = pjp.proceed();
        } catch (Throwable e) {
           
            if (!(e instanceof ConstraintViolationException) && !(e.getCause() instanceof ConstraintViolationException)
                    && !(e instanceof ChatterbotException) && !(e.getCause() instanceof ChatterbotException) &&
                    !(e instanceof ChatterbotServiceException) && !(e.getCause() instanceof ChatterbotServiceException)
                    && !(e instanceof HibernateOptimisticLockingFailureException)) {
                /* Persistir el error */
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                logger.error(e.getMessage() + " - " + outputStream.toString());
                e.printStackTrace(new PrintStream(outputStream));
                try {
                    errorLogDAO.save(new ErrorLog(e.getMessage(), outputStream.toString(), new Date()));
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            } else {
                throw e;
            }
        }
        return retVal;
    }
}

package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ErrorLogDAOTest extends GenericDAOTest<ErrorLog> {

    @Autowired
    private ErrorLogDAO errorLogDAO;

    private ErrorLog[] errorLogs;
    private Date[] dates;

    @Override
    protected GenericDAO<ErrorLog, Long> getDao() {
        return errorLogDAO;
    }

    @Override
    protected ErrorLog getSample() {
        return new ErrorLog("testMessage", "testStackTracte", new GregorianCalendar(2008, 10, 10).getTime());
    }

    @Override
    protected void updateSample(ErrorLog sample) {
        sample.setMessage("testMessage2");
    }

    @Before
    public void setup() {

        errorLogs = new ErrorLog[4];

        errorLogs[0] = new ErrorLog("testMessage1", "testSt1", new GregorianCalendar(2008, 01, 10).getTime());
        errorLogs[1] = new ErrorLog("testMessage2", "testSt2", new GregorianCalendar(2008, 01, 10).getTime());
        errorLogs[2] = new ErrorLog("testMessage3", "testSt3", new GregorianCalendar(2008, 01, 12).getTime());
        errorLogs[3] = new ErrorLog("testMessage4", "testSt4", new GregorianCalendar(2008, 01, 13).getTime());
        
        errorLogDAO.save(errorLogs[0]);
        errorLogDAO.save(errorLogs[1]);
        errorLogDAO.save(errorLogs[2]);
        errorLogDAO.save(errorLogs[3]);
        
        dates = new Date[] { new GregorianCalendar(2008, 01, 01).getTime(),
                new GregorianCalendar(2008, 01, 9).getTime(),
                new GregorianCalendar(2008, 01, 10).getTime(),
                new GregorianCalendar(2008, 01, 11).getTime(),
                new GregorianCalendar(2008, 01, 16).getTime()};

    }

    @Test
    public void testFindErrorLogs() {
        Collection<ErrorLog> actualResult, expectedResult;
        
        actualResult = errorLogDAO.findErrorLogs(new IntervalCriteria(dates[0], dates[1]));
        assertThat(actualResult.size(), equalTo(0));

        actualResult = errorLogDAO.findErrorLogs(new IntervalCriteria(dates[1], dates[2]));
        expectedResult = Arrays.asList(errorLogs[0], errorLogs[1]);
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = errorLogDAO.findErrorLogs(new IntervalCriteria(dates[1], dates[3]));
        expectedResult = Arrays.asList(errorLogs[0], errorLogs[1]);
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));
        
        actualResult = errorLogDAO.findErrorLogs(new IntervalCriteria(dates[2], dates[4]));
        expectedResult = Arrays.asList(errorLogs[0], errorLogs[1], errorLogs[2], errorLogs[3]);
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));
        
    }
}

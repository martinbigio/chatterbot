package ar.edu.itba.tpf.chatterbot.dao;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;

import org.junit.Test;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.test.context.transaction.TransactionConfiguration;

import ar.edu.itba.tpf.chatterbot.model.PersistentEntity;

@ContextConfiguration(locations = { "classpath:impl-data.xml", "classpath:impl-beans.xml" })
@TransactionConfiguration(transactionManager = "transactionManager")
public abstract class GenericDAOTest<T extends PersistentEntity> extends AbstractTransactionalJUnit4SpringContextTests {

    private GenericDAO<T, Long> dao;

    @BeforeTransaction
    public void setupTransaction() throws Exception {
        this.dao = getDao();
        assertThat(this.dao, notNullValue());
    }

    @Test
    public void testSaveAndFind() {
        T sample1 = getSample();
        T sample2 = getSample();

        Long id;

        assertThat(sample1, equalTo(sample2));
        dao.save(sample1);
        id = sample1.getId();
        assertThat(id, notNullValue());
        sample1 = dao.find(id);
        assertThat(sample1, equalTo(sample2));

        updateSample(sample1);
        assertThat(sample1, not(equalTo(sample2)));
        updateSample(sample2);
        assertThat(sample1, equalTo(sample2));

        dao.save(sample1);
        sample1 = dao.find(id);
        assertThat(sample1, equalTo(sample2));
    }

    @Test
    public void testFindAll() {
        Collection<T> result = dao.findAll();
        T sample = getSample();
        int length = result.size();

        dao.save(sample);
        result = dao.findAll();

        assertThat(result.size(), equalTo(length + 1));
        int count = 0;
        for (T t : result) {
            if (t.getId().equals(sample.getId()))
                count++;
        }
        assertThat(count, equalTo(1));
    }

    @Test
    public void testDelete() {
        Collection<T> result = dao.findAll();
        T sample = getSample();
        int length = result.size();

        dao.save(sample);
        result = dao.findAll();
        assertThat(result.size(), equalTo(length + 1));

        sample = dao.find(sample.getId());
        dao.delete(sample);
        result = dao.findAll();
        assertThat(result.size(), equalTo(length));
    }

    /**
     * Obtiene el dao a ser testeado. Cada clase que testee un dao específico debe retornar a través de este método la
     * instancia del dao a ser testeada.
     * 
     * @return El dao que se quiere testear.
     */
    protected abstract GenericDAO<T, Long> getDao();

    /**
     * Obtiene una instancia de la clase de dominio que maneja el dao testeado. Cada invocación debe retornar una nueva
     * instancia. El objeto retornado no debe tener id asignado. Cada invocación debe retornar una nueva instancia, pero
     * siempre con los mismos datos. Es decir: getSample().equals(getSample()) debe dar true.
     * 
     * @return Una instancia de la clase de dominio del dao.
     */
    protected abstract T getSample();

    /**
     * Modifica el objeto de modelo de dominio, de manera tal de que falle el equals entre la version anterior y la
     * posterior a la invocación.
     * 
     * @param sample Objeto a ser modificado.
     */
    protected abstract void updateSample(T sample);

}

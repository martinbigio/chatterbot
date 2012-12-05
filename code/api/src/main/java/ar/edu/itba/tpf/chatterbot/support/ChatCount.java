package ar.edu.itba.tpf.chatterbot.support;

/**
 * Clase de soporte para reportes que involucran contar atributos de un chat.
 * 
 * @param <T> Tipo del objeto que se cuenta.
 */
public class ChatCount<T> {

    private T criteria;
    private long count;

    /**
     * Crea una clase de soporte para reportes que involucran contar atributos de un chat.
     * 
     * @param criteria Objeto que se cuenta.
     * @param count Cantidad de ocurrencias del objeto.
     */
    public ChatCount(T criteria, long count) {
        super();
        this.criteria = criteria;
        this.count = count;
    }

    /**
     * @return Criterio.
     */
    public T getCriteria() {
        return criteria;
    }

    /**
     * @return Ocurrencias.
     */
    public long getCount() {
        return count;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (count ^ (count >>> 32));
        result = prime * result + ((criteria == null) ? 0 : criteria.hashCode());
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatCount other = (ChatCount) obj;
        if (count != other.count)
            return false;
        if (criteria == null) {
            if (other.criteria != null)
                return false;
        } else if (!criteria.equals(other.criteria))
            return false;
        return true;
    }
    
    
}

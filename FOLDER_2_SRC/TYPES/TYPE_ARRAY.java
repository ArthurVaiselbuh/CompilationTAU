package TYPES;

public class TYPE_ARRAY extends TYPE
{
    /***********************************/
    /* The type of the array's elements */
    /***********************************/
    public TYPE elementsType;

    /****************/
    /* CTROR(S) ... */
    /****************/
    public TYPE_ARRAY(String name, TYPE elementsType)
    {
        this.name = name;
        this.elementsType = elementsType;
    }

    /*************/
    /* isClass() */
    /*************/
    public boolean isClass(){ return false;}

    /*************/
    /* isArray() */
    /*************/
    public boolean isArray(){ return true;}
}
package ctd.net.rpc;



public class Result extends Payload{
	private static final long serialVersionUID = -6229596616525453018L;
	private Object result;
    private Throwable exception;
    
    public Result(){
    	
    }
    
    public Result(Object result){
        this.result = result;
    }

    public Result(Throwable exception){
        this.exception = exception;
    }
    
    public Object getValue() {
        return result;
    }

    public void setValue(Object value) {
        this.result = value;
    }

    public Throwable getException() {
        return exception;
    }

    public void setException(Throwable e) {
        this.exception = e;
    }
    
    public void throwExpceptionIfHas() throws Throwable{
    	 if (exception != null) {
             throw exception;
         }
    }

}

package dictionary.dictionaryjavafx;


public class Expression {
  private String expression;

  private boolean userCreated = false;

  Expression (String expression, Boolean userCreated) {
    this.expression = expression;
    this.userCreated = userCreated;
  }

  Expression (String expression) {
    this.expression = expression;
  }

  public String getExpression() {
    return expression;
  }

  public void setExpression(String expression) {
    this.expression = expression;
  }

  public boolean isUserCreated() {
    return userCreated;
  }

  @Override
  public String toString() {
    return getExpression() + (userCreated ? "👤": "");
  }

  /*
  public static void main(String[] args) throws InterruptedException {
    long begin = System.nanoTime();
    ArrayList<Expression> expressions = new ArrayList<>();
    for (int i = 0; i < 1000; ++i) {
      Expression e = new Expression(String.valueOf(i), true);
      expressions.add(e);
    }
    for(Expression i : expressions){
      System.out.println(i.expression);
    }
    System.out.println((double)(System.nanoTime() - begin)/1000000000);
  }
  */
}

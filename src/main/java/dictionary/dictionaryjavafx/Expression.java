package dictionary.dictionaryjavafx;


public class Expression {
  private String expression;
  private String pronunciation;
  private String meaning;

  private boolean userCreated = false;

  Expression (String expression, String pronunciation, String meaning, Boolean userCreated) {
    this.expression = expression;
    this.userCreated = userCreated;
    this.pronunciation = pronunciation;
    this.meaning = meaning;
  }

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

  public String getPronunciation() {return pronunciation;}

  public String getMeaning() {return meaning;}

  public boolean isUserCreated() {
    return userCreated;
  }

  @Override
  public String toString() {
    return (userCreated ? "👤 ": "") + getExpression();
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

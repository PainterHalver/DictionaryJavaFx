package dictionary.dictionaryjavafx;

public class ExpressionHolder {
  // This is the so-called Singleton class

  private Expression expression;
  private final static ExpressionHolder INSTANCE = new ExpressionHolder();

  private ExpressionHolder() {}

  public static ExpressionHolder getInstance() {
    return INSTANCE;
  }

  public void setExpression(Expression e) {
    this.expression = e;
  }

  public Expression getExpression() {
    return this.expression;
  }
}

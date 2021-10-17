package dictionary.dictionaryjavafx;


public class Expression {
  private String expression;
  private String pronunciation;
  private String meaning;
  private String id;
  private long createdAt;
  private long lastModified;

  private boolean userCreated = false;
  private boolean favourite = false;

  public Expression (String expression, String pronunciation, String meaning, Boolean userCreated) {
    this.expression = expression;
    this.userCreated = userCreated;
    this.pronunciation = pronunciation;
    this.meaning = meaning;
  }

  public Expression(String expression, String pronunciation, String meaning, Boolean userCreated, long createdAt, long lastModified) {
    this.expression = expression;
    this.userCreated = userCreated;
    this.pronunciation = pronunciation;
    this.meaning = meaning;
    this.createdAt  = createdAt;
    this.lastModified = lastModified;
  }

  Expression (String expression, Boolean favourite, String id) {
    this.expression = expression;
    this.favourite = favourite;
    this.id = id;
  }

  Expression (String expression, String id) {
    this.expression = expression;
    this.id = id;
  }

  Expression (String expression) {
    this.expression = expression;
  }

  public String getId() {
    return id;
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

  public long getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(long createdAt) {
    this.createdAt = createdAt;
  }

  public long getLastModified() {
    return lastModified;
  }

  public void setLastModified(long lastModified) {
    this.lastModified = lastModified;
  }

  public boolean isFavourite() {
    return favourite;
  }

  @Override
  public String toString() {
//    return (favourite ? "⭐ ": "") + (userCreated ? "👤 ": "") + getExpression();
    return this.expression;
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

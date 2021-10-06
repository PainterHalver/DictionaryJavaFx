package dictionary.dictionaryjavafx;

public final class Constants {


  private Constants() {
    // restrict instantiation
  }

  public static final int QUERY_TIMEOUT = 5; //second
  public static final String INIT_QUERY = "hello";
  public static final String NO_EXPRESSIONS_FOUND = "<h3>Sorry, no expressions found! <br> Maybe try using Google Translate instead?</h3>";
  public static final String GOOGLE_API_LOADING_TEXT = "<h3>Sending it to Google, please wait... :)</h3>";
  public static final String GOOGLE_SCRIPT_URL = "https://script.google.com/macros/s/AKfycbyX0vgobPcX82_GwJ7wrjqopu1KkL3rM2AVS6jZg_kuiB9YUqVSezvuWiRUCDdl5QSh/exec";
  public static final String GOOGLE_TTS_URL = "https://translate.googleapis.com/translate_tts?client=gtx&ie=UTF-8&tl=en&q=";
  public static final String VOICERSS_TTS_URL = "http://api.voicerss.org/?key=3b347642874c46b8a91b3b6373ad9916&hl=en-us&v=Mary&src=";

}

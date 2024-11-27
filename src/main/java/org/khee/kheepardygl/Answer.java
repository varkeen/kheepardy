package org.khee.kheepardygl;

public class Answer {
  private String answer;
  private String question;
  private String notes;

  public Answer() {
  }

  public Answer(String answer, String question, String notes) {
    this.answer = answer;
    this.question = question;
    this.notes = notes;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }

  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }
}

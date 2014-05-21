package com.wordnik.swagger.sample.model;

public class InputOption {
  private String name;
  private String description;
  private Boolean required;
  private String defaultValue;

  public InputOption() {}

  public InputOption(String name, String description, String defaultValue, Boolean required) {
    this.name = name;
    this.description = description;
    this.defaultValue = defaultValue;
    this.required = required;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setRequired(Boolean required) {
    this.required = required;
  }

  public Boolean getRequired() {
    return required;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue;
  }

  public String getDefaultValue() {
    return defaultValue;
  }
}

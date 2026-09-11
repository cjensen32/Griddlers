package com.connorjensen.griddlers.model;

import java.util.UUID;

public class Griddler {
  private UUID id;
  private Long size;

  public Griddler() {
    this.id = UUID.randomUUID();
    this.size = 10L;
  }

  public Griddler(Long size) {
    this.id = UUID.randomUUID();
    this.size = size;
  }
}

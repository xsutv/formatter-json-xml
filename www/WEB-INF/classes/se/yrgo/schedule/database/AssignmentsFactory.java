package se.yrgo.schedule.database;

import se.yrgo.schedule.domain.*;

public class AssignmentsFactory {
  
  private AssignmentsFactory() {}

  public static Assignments getAssignments() {
    return new DatabaseAssignments();
  }
  
}

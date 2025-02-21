package se.yrgo.schedule.formatters;

import java.util.List;
import se.yrgo.schedule.domain.*;

public interface Formatter {

  public String format(List<Assignment> assignments);
  
}

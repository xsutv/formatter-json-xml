package se.yrgo.schedule.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.*;
import javax.servlet.http.*;

import se.yrgo.schedule.database.*;
import se.yrgo.schedule.domain.*;
import se.yrgo.schedule.exceptions.*;
import se.yrgo.schedule.formatters.*;
import se.yrgo.schedule.servlet.ParamParser.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * <p>Listens to requests on localhost:8080/v1/ and accepts the following parameters:
 * <ul>
 * <li> none - lists all schedules for all teachers </li>
 * <li> substitute_id - the ID for a substitute teacher you want to list the schedult for</li>
 * <li> day - the day (YYYY-mm-dd) you want to see the schedule for</li>
 * </ul>
 * <p>The substitute_id and day parameters can be combined or used alone.</p>
 */
public class ScheduleServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

      // Read the request as UTF-8
      request.setCharacterEncoding(UTF_8.name());

      // Catches an illegalArgumentException and responds with a bad request if parser is handled incorrectly (format). 
      ParamParser parser = null; 
      try {
        parser = new ParamParser(request);

      } catch (IllegalArgumentException e) {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        return;
      }

      response.setContentType(parser.contentType());
      response.setCharacterEncoding(UTF_8.name());

      Assignments db = AssignmentsFactory.getAssignments();

      List<Assignment> assignments = new ArrayList<>();

      try {
        // StringBuilder table;
        switch (parser.type()){
          case ALL:
            assignments = db.all();
            break;
          case TEACHER_ID_AND_DAY:
            assignments = db.forTeacherAt(parser.teacherId(), parser.day());
            break;
          case DAY:
            assignments = db.at(parser.day());
            break;
          case TEACHER_ID:
            assignments = db.forTeacher(parser.teacherId());
        }

      } catch (AccessException e) {
        sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database access error" + e.getMessage());
      }

      try (PrintWriter out = response.getWriter();){ 

      Formatter formatter = FormatterFactory.getFormatter(parser.format());
      String result = formatter.format(assignments);

      // If no assignments from the asked day exists. 404 Not found is given. 
      if (assignments.isEmpty() && parser.type() == (QueryType.DAY)) {
        sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "No assignments found for this day.");
        return;
      } 

      out.println(result);

      } catch (IllegalArgumentException e) {
        sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
      }
      
    }

    // Error utility method to get rid of repetitive code.
    private void sendJsonError(HttpServletResponse response, int status, String message) throws IOException {
      response.setStatus(status);
      response.setContentType("application/json");
      try (PrintWriter out = response.getWriter()) {
          out.println("{\"error\": \"" + message + "\"}");
      }
  }

}

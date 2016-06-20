import model.TODO;
import spark.Request;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.List;

import static spark.Spark.*;
import static util.SparkExtensions.*;
import static util.SparkExtensions.Intercooler.*;

public class Server
{
  public static void main( String[] args )
  {
    // Server set up
    setTemplateEngine( new HandlebarsTemplateEngine() );
    staticFileLocation( "/public" );
    initIntercooler();

    // render main UI
    get( "/", ( request, response ) -> {
      return renderTodos( request );
    } );

    // add a new TODO
    post( "/todos", ( request, response ) -> {
      TODO.add( new TODO( request.queryParams( "todo" ) ) );
      return renderTodos( request );
    } );

    // remove completed TODOS
    delete( "/todos/completed", ( request, response ) -> {
      for( TODO todo : TODO.ofStatus( TODO.Status.complete ) )
      {
        TODO.remove( todo.getId() );
      }
      return renderTodos( request );
    } );

    // Toggle all TODO status
    put( "/todos/toggle_status", ( request, response ) -> {
      for( TODO todo : TODO.all() )
      {
        todo.setStatus( request.queryParams( "complete" ) != null ? TODO.Status.complete : TODO.Status.active );
      }
      return renderTodos( request );
    } );

    // remove a new TODO
    delete( "/todos/:id", ( request, response ) -> {
      TODO.remove( request.params( "id" ) );
      return renderTodos( request );
    } );

    // Save TODO
    put( "/todos/:id", ( request, response ) -> {
      TODO todo = TODO.find( request.params( "id" ) );
      todo.setTitle( request.queryParams( "todo" ) );
      return renderTodos( request );
    } );

    // Toggle TODO status
    put( "/todos/:id/toggle_status", ( request, response ) -> {
      TODO.find( request.params( "id" ) ).toggleStatus();
      return renderTodos( request );
    } );

    // Edit TODO
    get( "/todos/:id/edit", ( request, response ) -> {
      TODO todo = TODO.find( request.params( "id" ) );
      return renderTemplate( "edit.html", arg( "todo", todo ) );
    } );

  }

  private static Object renderTodos( Request request )
  {
    String statusStr = request.queryParams( "status" );
    List<TODO> todos = isEmpty( statusStr) ? TODO.all() : TODO.ofStatus( TODO.Status.valueOf( statusStr ) );
    String filter = statusStr != null ? statusStr : "all";
    int activeCount = TODO.ofStatus( TODO.Status.active ).size();
    int completeCount = TODO.ofStatus( TODO.Status.complete ).size();
    boolean completeTodos = completeCount > 0;
    boolean allComplete = completeCount > 0 && activeCount == 0;

    return renderTemplate( intercooler.isRequest() ? "main_section.html" : "index.html",
                           arg( "todos", todos ),
                           arg( filter, true ),
                           arg("activeCount", activeCount),
                           arg("completeTodos", completeTodos),
                           arg("allComplete", allComplete),
                           arg("status", statusStr)
                           );
  }

  private static boolean isEmpty( String statusStr )
  {
    return statusStr == null || statusStr.isEmpty();
  }
}

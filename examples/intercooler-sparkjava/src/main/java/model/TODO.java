package model;

import org.omg.PortableInterceptor.ACTIVE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class TODO
{

  public enum Status
  {
    active,
    complete
  }

  //----------------------------
  //  Datastore
  //----------------------------
  private static final TodoList DATA = new TodoList();
  public static void add(TODO todo) {
    DATA.add( todo );
  }

  public static TODO find(String id) {
    for( TODO todo : DATA )
    {
      if( todo.getId().equals( id ) )
      {
        return todo;
      }
    }
    return null;
  }

  public static TodoList ofStatus(Status status) {
    TodoList todos = new TodoList();
    for( TODO todo : DATA )
    {
      if( todo.getStatus().equals( status ) )
      {
        todos.add( todo );
      }
    }
    return todos;
  }

  public static void remove(String id) {
    Iterator<TODO> iterator = DATA.iterator();
    while( iterator.hasNext() )
    {
      TODO next = iterator.next();
      if( next.getId().equals( id ) )
      {
        iterator.remove();
      }
    }
  }

  public static TodoList all()
  {
    return DATA;
  }

  //----------------------------
  //  Model
  //----------------------------

  private final String _id;
  private String _title;
  private Status _status;

  public TODO( String title )
  {
    _id = UUID.randomUUID().toString();
    _title = title;
    _status = Status.active;
  }

  public String getId() {
    return _id;
  }

  public String getTitle() {
    return _title;
  }

  public void setTitle( String title ) {
    _title = title;
  }

  public Status getStatus() {
    return _status;
  }

  public void setStatus(Status s) {
    _status = s;
  }

  public boolean isComplete() {
    return _status == Status.complete;
  }

  public void toggleStatus()
  {
    _status = (_status == Status.complete) ? Status.active : Status.complete;
  }
}

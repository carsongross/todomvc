package util;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateEngine;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SparkExtensions
{
  private static TemplateEngine TEMPLATE_ENGINE;

  public static void setTemplateEngine( TemplateEngine templateEngine )
  {
    TEMPLATE_ENGINE = templateEngine;
  }

  public static TemplateArg arg( String name, Object val )
  {
    return new TemplateArg( name, val );
  }

  public static String renderTemplate( String template, TemplateArg... args ) {
    Map attributes = new HashMap<>();
    for( int i = 0; i < args.length; i++ )
    {
      TemplateArg arg = args[i];
      attributes.put( arg.getName(), arg.getVal() );
    }
    ModelAndView modelAndView = new ModelAndView( attributes, template );
    return TEMPLATE_ENGINE.render( modelAndView );
  }

  public static class TemplateArg
  {
    private final String _name;
    private final Object _val;

    public TemplateArg( String name, Object val )
    {
      _name = name;
      _val = val;
    }

    public String getName()
    {
      return _name;
    }

    public Object getVal()
    {
      return _val;
    }
  }

  static public class Intercooler
  {
    private static final Intercooler INSTANCE = new Intercooler();
    private static final ThreadLocal<Request> REQ = new ThreadLocal<>();
    private static final ThreadLocal<Response> RESP = new ThreadLocal<>();
    private Intercooler() {}

    //============================================================
    // Public API
    //============================================================
    public static final Intercooler intercooler = INSTANCE;

    public static void initIntercooler()
    {
      Spark.before( INSTANCE::before );
      Spark.after( ( request, response ) -> {
        INSTANCE.after();
      } );
    }

    public boolean isRequest()
    {
      return Objects.equals( REQ.get().queryParams( "ic-request" ), "true" ) ||
             Objects.equals( REQ.get().params( "ic-request" ), "true" );
    }

    public boolean targetIs( String id )
    {
      return Objects.equals( REQ.get().queryParams( "ic-target" ), id ) ||
             Objects.equals( REQ.get().params( "ic-target" ), id );
    }

    public void trigger( String event )
    {
      RESP.get().header( "X-IC-Trigger", event );
    }

    //============================================================
    // Internals
    //============================================================
    private void before( Request request, Response response )
    {
      REQ.set( request );
      RESP.set( response );
    }

    private void after()
    {
      REQ.remove();
      RESP.remove();
    }
  }
}

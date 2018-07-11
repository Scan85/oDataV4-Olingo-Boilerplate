/**
 * 
 */
package odataservice.odatav4.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import odataservice.odatav4.data.Storage;
import odataservice.odatav4.service.EdmProvider;
import odataservice.odatav4.service.EntityCollectionProcessor;
import odataservice.odatav4.service.EntityProcessor;
import odataservice.odatav4.service.PrimitiveProcessor;

import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Seyit Can
 *
 */
public class Servlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	  private static final Logger LOG = LoggerFactory.getLogger(Servlet.class);

	  @Override
	  protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	    try {
	      HttpSession session = req.getSession(true);
	      Storage storage = (Storage) session.getAttribute(Storage.class.getName());
	      if (storage == null) {
	        storage = new Storage();
	        session.setAttribute(Storage.class.getName(), storage);
	      }

	      // create odata handler and configure it with EdmProvider and Processor
	      OData odata = OData.newInstance();
	      ServiceMetadata edm = odata.createServiceMetadata(new EdmProvider(), new ArrayList<EdmxReference>());
	      ODataHttpHandler handler = odata.createHandler(edm);
	      handler.register(new EntityCollectionProcessor(storage));
	      handler.register(new EntityProcessor(storage));
	      handler.register(new PrimitiveProcessor(storage));

	      // let the handler do the work
	      handler.process(req, resp);
	    } catch (RuntimeException e) {
	      LOG.error("Server Error occurred in DemoServlet", e);
	      throw new ServletException(e);
	    }

	  }

}

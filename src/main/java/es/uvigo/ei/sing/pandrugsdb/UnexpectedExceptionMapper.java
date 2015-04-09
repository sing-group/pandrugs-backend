package es.uvigo.ei.sing.pandrugsdb;

import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import es.uvigo.ei.sing.pandrugsdb.service.entity.ErrorMessage;

@Provider
public class UnexpectedExceptionMapper implements ExceptionMapper<Throwable> {
	private final Logger LOG = LoggerFactory.getLogger(UnexpectedExceptionMapper.class);
	
	@Override
	public Response toResponse(Throwable exception) {
		System.out.println("Exception: " + exception);
		if (exception instanceof WebApplicationException) {
			return ((WebApplicationException) exception).getResponse();
		} else {
			LOG.error("Unexpected exception", exception);
			
			return Response.status(INTERNAL_SERVER_ERROR)
				.entity(new ErrorMessage(INTERNAL_SERVER_ERROR.getStatusCode(), exception.getMessage()))
			.build();
		}
	}
}

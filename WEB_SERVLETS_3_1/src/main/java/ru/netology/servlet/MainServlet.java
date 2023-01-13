package ru.netology.servlet;

import ru.netology.controller.PostController;
import ru.netology.exception.NotFoundException;
import ru.netology.repository.PostRepository;
import ru.netology.service.PostService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MainServlet extends HttpServlet {
	private PostController controller;

	@Override
	public void init() {
		final var repository = new PostRepository();
		final var service = new PostService(repository);
		controller = new PostController(service);
	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			final var path = req.getRequestURI();
			final var method = req.getMethod();
			if (method.equals("GET") && path.equals("/api/posts")) {
				controller.all(resp);
				return;
			}
			final String[] lastArg = path.substring(path.lastIndexOf("/")).split("");
			if (method.equals("GET") && path.matches("/api/posts/\\d+")) {
				final var id = Long.parseLong(lastArg[1]);
				controller.getById(id, resp);
				return;
			}
			if (method.equals("POST") && path.equals("/api/posts")) {
				controller.save(req.getReader(), resp);
				return;
			}
			if (method.equals("DELETE") && path.matches("/api/posts/\\d+")) {
				final var id = Long.parseLong(lastArg[1]);
				controller.removeById(id, resp);
				return;
			}
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		} catch (NotFoundException exception) {
			badRequest(resp, exception);
		} catch (Exception e) {
			e.printStackTrace();
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	private void badRequest(HttpServletResponse response, NotFoundException exception) throws IOException {
		response.setStatus(404);
	}
}


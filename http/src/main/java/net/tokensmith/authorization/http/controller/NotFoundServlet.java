package net.tokensmith.authorization.http.controller;

import org.springframework.stereotype.Controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by tommackenzie on 3/17/15.
 */
@Controller
@WebServlet(value="/notFound", name="notFoundServlet")
public class NotFoundServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.getRequestDispatcher("/WEB-INF/jsp/404.jsp").forward(req, resp);
        return;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
        req.getRequestDispatcher("/WEB-INF/jsp/404.jsp").forward(req, resp);
        return;
    }
}

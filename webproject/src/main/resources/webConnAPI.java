import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.*;
@SuppressWarnings("serial")
public class webConnAPI extends HttpServlet {
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println(request.getParameter("name"));
}
package system.controllers;

import system.annotations.Browseable;
import system.exceptions.UrlNotSupportedException;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import system.utils.ClassMethod;
import system.utils.ModelView;
import system.utils.ServletManager;

@WebServlet(name = "FrontServlet", urlPatterns = {"*.do"})
public class FrontServlet extends HttpServlet {

    public void init() throws ServletException {

        ServletContext context = getServletContext();
        HashMap<String, ClassMethod> assoc = new HashMap<>();

        try {
            assoc = ServletManager.fillData("model");
        } catch (Exception e) {
        }

        context.setAttribute("assoc", assoc);

    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        PrintWriter out = response.getWriter();
        ServletContext context = getServletContext();
        ServletManager servletManager = new ServletManager();
        String uri = request.getRequestURI();
        String url = servletManager.retrieveUrlFromRawurl(uri);
        HashMap<String, ClassMethod> assoc = (HashMap<String, ClassMethod>) context.getAttribute("assoc");
        RequestDispatcher dispat = null;

        try {
            if (assoc == null) {
                assoc = ServletManager.fillData("model");
            }
            out.println(assoc + "<br>");
            ClassMethod classMethod = null;
            if (servletManager.checkUrl(url, assoc)) {
                classMethod = assoc.get(url);
//                out.println(classMethod + "<br>");
//                out.println(classMethod.getClassName() + "<br>");
//                out.println(classMethod.getMethodName() + "<br>");
//                out.println("Good Url !" + "<br>");

                Object obj = Class.forName(classMethod.getClassName()).newInstance();

                Field[] tabFields = obj.getClass().getDeclaredFields();
                for (int i = 0; i < tabFields.length; i++) {
                    Field field = tabFields[i];
                    String fieldName = field.getName();
                    String typeName = field.getType().getSimpleName();

                    if (field.isAnnotationPresent(Browseable.class)) {
                        Object formData = request.getParameter(fieldName);
                        
                        if (typeName.equalsIgnoreCase("Integer")) {
                            formData = Integer.parseInt((String) formData);
                        } else if (typeName.equalsIgnoreCase("Double")) {
                            formData = Double.parseDouble((String) formData);
                        } else if (typeName.equalsIgnoreCase("Float")) {
                            formData = Float.parseFloat((String) formData);
                        } else {
                            formData = (String) formData;
                        }
                        obj.getClass().getMethod(ServletManager.createSetter(fieldName), field.getType()).invoke(obj, formData);
                    }
                }

                out.println(obj.getClass().getName() + "<br>");
                Method method = obj.getClass().getMethod(classMethod.getMethodName());
                out.println(classMethod.getMethodName() + "<br>");
                ModelView modelView = (ModelView) method.invoke(obj);
                HashMap data = modelView.getData();
                request.setAttribute("data", data);

                dispat = request.getRequestDispatcher(modelView.getUrl());
                dispat.forward(request, response);

            }
        } catch (UrlNotSupportedException e) {
//            out.println(e);
            dispat = request.getRequestDispatcher("404.html");
            dispat.forward(request, response);
        } catch (ClassNotFoundException e) {
            out.println(e);
        } catch (Exception e) {
            out.println(e);
            e.printStackTrace();
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

}

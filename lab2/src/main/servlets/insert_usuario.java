package servlets;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author pmegias
 */
@WebServlet(urlPatterns = {"/DB_Servlet"})
public class insert_usuario extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
       Connection connection = null;
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            String query, orden, test, tabla;
            PreparedStatement statement = null;
            
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            // create a database connection
            connection = DriverManager.getConnection("jdbc:derby://localhost:1527/PR2;user=pr2;password=pr2");
            

            orden = request.getParameter("action");
            tabla = request.getParameter("tabla");
            
            out.println("El comando recibido por la BD es --> "+orden+" "+tabla);
            
            orden = "CREATE";
            
            
            if (orden.equals("CREATE")){
                if(tabla.equals("usuarios") || tabla.equals("ambas")){
                    test = "create table usuarios (id_usuario varchar (256) primary key, password varchar (256))";
                    statement = connection.prepareStatement(test);  
                    statement.executeUpdate();

                    query = "insert into usuarios values(?,?)";
                    statement = connection.prepareStatement(query);
                    statement.setString(1, "Pepito");
                    statement.setString(2, "23456");                                    
                    statement.executeUpdate();
                    
                    statement.setString(1, "Margarita");
                    statement.setString(2, "12547");                                    
                    statement.executeUpdate();
                }

                // With preparedStatement, SQL Injection and other problems when inserting values in the database can be avoided
                if(tabla.equals("image") || tabla.equals("ambas")){
                     query = "create table image (id int NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), "
                            + "title varchar (256) NOT NULL, description varchar (1024) NOT NULL, keywords "
                            + "varchar (256) NOT NULL, author varchar (256) NOT NULL, creator varchar (256) NOT NULL, "
                            + "capture_date varchar (10) NOT NULL, storage_date varchar (10) NOT NULL, filename varchar (512) NOT NULL, "
                            + "primary key (id), foreign key (author) references usuarios(id_usuario))";

                    statement = connection.prepareStatement(query);
                    statement.executeUpdate();
                    
                    query = "INSERT INTO image (TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                    statement = connection.prepareStatement(query);           
                    statement.setString(1, "test1");
                    statement.setString(2, "This is image 1");
                    statement.setString(3, "Keyword11, Keyword12");
                    statement.setString(4, "Margarita");
                    statement.setString(5, "Margarita");
                    statement.setString(6, "2020/03/02");
                    statement.setString(7, "2020/09/17");
                    statement.setString(8, "file1.jpg");            
                    statement.executeUpdate();      
                              
                    statement.setString(1, "test2");
                    statement.setString(2, "This is image 2");
                    statement.setString(3, "Keyword21, Keyword22");
                    statement.setString(4, "Pepito");
                    statement.setString(5, "Pepito");
                    statement.setString(6, "2019/03/02");
                    statement.setString(7, "2020/09/17");
                    statement.setString(8, "file2.jpg");            
                    statement.executeUpdate();
                }
                    
            }
            /* 
               END COMMENT 
            */            
            
            // Select information from users and image and show in the web
            if (orden.equals("SHOW")){
                if(tabla.equals("usuarios") || tabla.equals("ambas")){
                    out.println("<h1>Contenido de la tabla Usuarios</h1>");
                    query = "select * from usuarios";
                    statement = connection.prepareStatement(query);
                    ResultSet rs = statement.executeQuery();    

                    while (rs.next()) {
                        // read the result set
                        out.println("<br>Usuario / Passwd --> " + rs.getString("id_usuario"));
                        out.println(" / " + rs.getString("password"));
                    }
                }
                if(tabla.equals("image") || tabla.equals("ambas")){
                    out.println("<h1>Contenido de la tabla Image</h1>");
                    query = "select * from image";
                    statement = connection.prepareStatement(query);
                    ResultSet rs = statement.executeQuery();    

                    while (rs.next()) {
                        //(TITLE, DESCRIPTION, KEYWORDS, AUTHOR, CREATOR, CAPTURE_DATE, STORAGE_DATE, FILENAME)
                        // read the result set
                        out.println("<br>Id image = " + rs.getString("id"));
                        out.println("<ol>");
                            out.println("<li>Titulo = " + rs.getString("title"));
                            out.println("<li>Descripción = " + rs.getString("description"));
                            out.println("<li>Autor = " + rs.getString("author"));
                            out.println("<li>Creador = " + rs.getString("creator"));
                            out.println("<li>Fecha Captura = " + rs.getString("capture_date"));
                        out.println("</ol>");
                    }
                }
            }
            
            if (orden.equals("CLEAN")){
                if(tabla.equals("image") || tabla.equals("ambas")){
                    query = "delete from image";
                    statement = connection.prepareStatement(query);
                    statement.executeUpdate();
                }
                if(tabla.equals("usuarios") || tabla.equals("ambas")){
                    query = "delete from usuarios";
                    statement = connection.prepareStatement(query);
                    statement.executeUpdate();
                }
            }
            
            test = "<a href=\"crea_DB3.html\"><br> Link a la DB </a>";
            out.println(test);
            /*
            query = "select * from image";
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();                                    

            while (rs.next()) {
                // read the result set
                out.println("<br>Id image = " + rs.getString("id"));
                out.println("Titulo = " + rs.getString("title"));
            }*/
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SpesaWS;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

/**
 * REST Web Service
 *
 * @author Ziz
 */
@ApplicationPath("/")
@Path("spesa")

public class GenericResource extends Application {

    final private String driver = "com.mysql.jdbc.Driver";
    final private String dbms_url = "jdbc:mysql://localhost/";
    final private String database = "db_spesa";
    final private String user = "root";
    final private String password = "";
    private Connection spesaDatabase;
    private boolean connected;

    public GenericResource() {
        super();
    }

    public void init() {
        String url = dbms_url + database;
        try {
            Class.forName(driver);
            spesaDatabase = DriverManager.getConnection(url, user, password);
            connected = true;
        } catch (SQLException e) {
            connected = false;
        } catch (ClassNotFoundException e) {
            connected = false;
        }
    }

    public void destroy() {
        try {
            spesaDatabase.close();
        } catch (SQLException e) {
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("getLista")
    public String getLista(@QueryParam("idLista") String id) {

        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT Costo,Nome,Marca FROM prodotto p, lista l WHERE p.idProdotto = l.rifProdotto AND ";
            if (!id.isEmpty()) {
                sql = sql + " l.idLista='" + id;
            }

            Statement statement = spesaDatabase.createStatement();
            ResultSet result = statement.executeQuery(sql);

            ArrayList<Prodotto> spesa = new ArrayList(0);

            while (result.next()) {

                String costo = result.getString("costo");
                String marca = result.getString("marca");
                String nome = result.getString("nome");

                Prodotto prodotto = new Prodotto(costo, marca, nome);

                spesa.add(prodotto);
            }
            result.close();
            statement.close();

            output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
            output += "<listaSpesa>\n";

            if (!spesa.isEmpty()) {

                for (int i = 0; i < spesa.size(); i++) {
                    Prodotto p = spesa.get(i);

                    output += "<prodotto>\n";
                    output += "<costo>";
                    output += p.getCosto();
                    output += "</costo>\n";
                    output += "<nome>";
                    output += p.getNome();
                    output += "</nome>";
                    output += "</number>\n";
                    output += "<marca>";
                    output += p.getMarca();
                    output += "</marca>";
                    output += "</number>\n";
                    output += "</prodotto>\n";
                }

                output += "</listaSpesa>\n";
            } else {
                destroy();
                return "<errorMessage>404</errorMessage>";

            }

        } catch (SQLException ex) {
            destroy();
            return "<errorMessage>500</errorMessage>";
        }
        destroy();
        return output;
    }

}

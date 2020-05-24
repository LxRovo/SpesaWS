/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spesa;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.*;
import javax.ws.rs.*;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

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
    public String getLista(@QueryParam("rifRichiesta") String id) {

        init();
        String output = "";
        if (!connected) {
            return "<errorMessage>400</errorMessage>";
        }

        try {
            String sql = "SELECT Costo,Nome,Marca FROM prodotto p, lista l WHERE p.idProdotto = l.rifProdotto AND ";
            if (!id.isEmpty()) {
                sql = sql + " l.rifRichiesta='" + id + "'";
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
                    output += "</nome>\n";
                    output += "<marca>";
                    output += p.getMarca();
                    output += "</marca>\n";
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
    
    @POST
    @Consumes(MediaType.TEXT_XML)
    @Path("lista")
    public String postProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("lista.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

            ArrayList<Lista> liste = (ArrayList<Lista>) myParse.parseDocument("lista.xml", "post");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "INSERT INTO lista (rifRichiesta, rifProdotto, quantita) VALUES ('" + liste.get(0).getRifRichiesta()+ "','" + liste.get(0).getRifProdotto() + "','" + liste.get(0).getQuantita()+ "')";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Inserimento effettuato</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }
    
    @PUT
    @Consumes(MediaType.TEXT_XML)
    @Path("updLista")
    public String putProdotto(String content) {
        try {
            init();

            MyParser myParse = new MyParser();
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter("updLista.xml"));
            writer.write(content);
            writer.flush();
            writer.close();

             ArrayList<Lista> lista = (ArrayList<Lista>) myParse.parseDocument("updLista.xml", "put");
            if (!connected) {
                return "<errorMessage>400</errorMessage>";
            }

            try {
                String sql = "UPDATE lista SET rifRichiesta='" + lista.get(0).getRifRichiesta() + "', rifProdotto='" + lista.get(0).getRifProdotto() + "', quantita='" + lista.get(0).getQuantita() + "' WHERE idLista='" + lista.get(0).getIdLista() + "'";
                Statement statement = spesaDatabase.createStatement();

                if (statement.executeUpdate(sql) <= 0) {
                    statement.close();
                    return "<errorMessage>403</errorMessage>";
                }

                statement.close();
                destroy();
                return "<message>Update effettuato</message>";
            } catch (SQLException ex) {
                destroy();
                return "<errorMessage>500</errorMessage>";
            }
        } catch (IOException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(GenericResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "<errorMessage>400</errorMessage>";
    }
   

}

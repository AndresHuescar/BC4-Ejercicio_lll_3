import java.sql.*;
import java.util.Scanner;

public class Incidencias {

    static String url = "jdbc:oracle:thin:@localhost:1521:XE";
    static String user = "RIBERA";
    static String password = "ribera";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int op;

        do {
            System.out.println("\n1.Insertar 2.Ver todas 3.Ver por ciclista 0.Salir");
            op = sc.nextInt();

            try (Connection conn = DriverManager.getConnection(url, user, password)) {

                if (op == 1) {

                    System.out.print("ID ciclista: ");
                    int ciclista = sc.nextInt();

                    System.out.print("Etapa: ");
                    int etapa = sc.nextInt();

                    sc.nextLine();
                    System.out.print("Tipo: ");
                    String tipo = sc.nextLine();

                    conn.setAutoCommit(false); // iniciar transacción

                    try {
                        // comprobar ciclista
                        PreparedStatement c1 = conn.prepareStatement(
                                "SELECT 1 FROM CICLISTA WHERE ID_CICLISTA=?");
                        c1.setInt(1, ciclista);

                        if (!c1.executeQuery().next()) {
                            System.out.println("Ciclista no existe");
                            conn.rollback();
                            continue;
                        }

                        // comprobar etapa
                        PreparedStatement c2 = conn.prepareStatement(
                                "SELECT 1 FROM ETAPA WHERE NUMERO=?");
                        c2.setInt(1, etapa);

                        if (!c2.executeQuery().next()) {
                            System.out.println("Etapa no existe");
                            conn.rollback();
                            continue;
                        }

                        // insertar
                        PreparedStatement ps = conn.prepareStatement(
                                "INSERT INTO INCIDENCIA (ID_CICLISTA, NUMERO_ETAPA, TIPO) VALUES (?, ?, ?)");

                        ps.setInt(1, ciclista);
                        ps.setInt(2, etapa);
                        ps.setString(3, tipo);

                        ps.executeUpdate();

                        conn.commit();
                        System.out.println("ncidencia insertada");

                    } catch (Exception e) {
                        conn.rollback();
                        System.out.println("eror -> rollback");
                    }
                }

                if (op == 2) {

                    PreparedStatement ps = conn.prepareStatement(
                            "SELECT i.ID_INCIDENCIA, c.NOMBRE, i.NUMERO_ETAPA, i.TIPO " +
                                    "FROM INCIDENCIA i " +
                                    "JOIN CICLISTA c ON i.ID_CICLISTA = c.ID_CICLISTA");

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        System.out.println("[" + rs.getInt(1) + "] " +
                                rs.getString(2) + " - Etapa " +
                                rs.getInt(3) + " - " +
                                rs.getString(4));
                    }
                }

                if (op == 3) {

                    System.out.print("ID ciclista: ");
                    int id = sc.nextInt();

                    PreparedStatement ps = conn.prepareStatement(
                            "SELECT i.ID_INCIDENCIA, i.NUMERO_ETAPA, i.TIPO " +
                                    "FROM INCIDENCIA i " +
                                    "WHERE i.ID_CICLISTA=? " +
                                    "ORDER BY i.NUMERO_ETAPA");

                    ps.setInt(1, id);

                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        System.out.println("[" + rs.getInt(1) + "] Etapa " +
                                rs.getInt(2) + " - " +
                                rs.getString(3));
                    }
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

        } while (op != 0);

        sc.close();
    }
}
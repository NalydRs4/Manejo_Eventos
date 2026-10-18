package vista;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import controlador.logica_ventana;

public class ventana extends JFrame {

    public JPanel contentPane;
    public JTabbedPane tabbedPane;
    public JPanel panelContactos;
    public JPanel panelEstadisticas;
    
    public JTextField txt_nombres, txt_telefono, txt_email, txt_buscar;
    public JCheckBox chb_favorito;
    public JComboBox<String> cmb_categoria;
    public JButton btn_add, btn_modificar, btn_eliminar, btn_limpiar;
    public JTable tbl_contactos;
    public DefaultTableModel modeloTabla;
    public JProgressBar prg_carga;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                ventana frame = new ventana();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public ventana() {
        setTitle("GESTIÓN DE CONTACTOS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setBounds(100, 100, 1026, 600); 
        setLocationRelativeTo(null); 
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout(0, 0));

        // JTabbedPane para organizar la interfaz en pestañas
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        contentPane.add(tabbedPane, BorderLayout.CENTER);

        panelContactos = new JPanel();
        panelContactos.setLayout(null); 
        tabbedPane.addTab("Contactos", null, panelContactos, "Gestión de los contactos");

        JLabel lblNombres = new JLabel("NOMBRES:");
        lblNombres.setBounds(25, 30, 89, 20);
        lblNombres.setFont(new Font("Tahoma", Font.BOLD, 14));
        panelContactos.add(lblNombres);

        JLabel lblTelefono = new JLabel("TELÉFONO:");
        lblTelefono.setBounds(25, 70, 89, 20);
        lblTelefono.setFont(new Font("Tahoma", Font.BOLD, 14));
        panelContactos.add(lblTelefono);

        JLabel lblEmail = new JLabel("EMAIL:");
        lblEmail.setBounds(25, 110, 89, 20);
        lblEmail.setFont(new Font("Tahoma", Font.BOLD, 14));
        panelContactos.add(lblEmail);

        JLabel lblBuscar = new JLabel("BUSCAR CONTACTO:");
        lblBuscar.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblBuscar.setBounds(25, 470, 160, 20);
        panelContactos.add(lblBuscar);

        txt_nombres = new JTextField();
        txt_nombres.setBounds(124, 25, 427, 30);
        panelContactos.add(txt_nombres);

        txt_telefono = new JTextField();
        txt_telefono.setBounds(124, 65, 427, 30);
        panelContactos.add(txt_telefono);

        txt_email = new JTextField();
        txt_email.setBounds(124, 105, 427, 30);
        panelContactos.add(txt_email);

        txt_buscar = new JTextField();
        txt_buscar.setBounds(190, 465, 800, 30);
        panelContactos.add(txt_buscar);

        chb_favorito = new JCheckBox("CONTACTO FAVORITO");
        chb_favorito.setBounds(25, 150, 180, 20);
        chb_favorito.setFont(new Font("Tahoma", Font.BOLD, 13));
        panelContactos.add(chb_favorito);

        cmb_categoria = new JComboBox<>(new String[]{"Elija una Categoria", "Familia", "Amigos", "Trabajo"});
        cmb_categoria.setBounds(220, 145, 200, 30);
        panelContactos.add(cmb_categoria);

        btn_add = new JButton("AGREGAR");
        btn_add.setBounds(580, 25, 130, 40);
        panelContactos.add(btn_add);

        btn_modificar = new JButton("MODIFICAR");
        btn_modificar.setBounds(720, 25, 130, 40);
        panelContactos.add(btn_modificar);

        btn_eliminar = new JButton("ELIMINAR");
        btn_eliminar.setBounds(860, 25, 130, 40);
        panelContactos.add(btn_eliminar);
        
        btn_limpiar = new JButton("LIMPIAR CAMPOS");
        btn_limpiar.setBounds(580, 75, 410, 40);
        panelContactos.add(btn_limpiar);

        // Configuración de la tabla
        String[] columnas = {"Nombre", "Teléfono", "Email", "Categoría", "Favorito"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };
        
        tbl_contactos = new JTable(modeloTabla);
        tbl_contactos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrTabla = new JScrollPane(tbl_contactos);
        scrTabla.setBounds(25, 200, 965, 250); 
        panelContactos.add(scrTabla);

        // Barra de progreso
        prg_carga = new JProgressBar();
        prg_carga.setBounds(25, 505, 965, 15);
        prg_carga.setStringPainted(true); 
        prg_carga.setVisible(true); 
        panelContactos.add(prg_carga);

        panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(null); 
        tabbedPane.addTab("Estadísticas", null, panelEstadisticas, "Métricas e informes de contactos");
        
        JLabel lblEstadisticasTitle = new JLabel("Desarrollado Por: DYLAN TOTOY");
        lblEstadisticasTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        lblEstadisticasTitle.setBounds(330, 200, 400, 30);
        panelEstadisticas.add(lblEstadisticasTitle);

        new logica_ventana(this);
    }
}
package controlador;

import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities; 
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

import vista.ventana;
import modelo.persona;
import modelo.personaDAO;

public class logica_ventana implements ActionListener {
    private ventana delegado;
    private List<persona> contactos;
    private TableRowSorter<javax.swing.table.DefaultTableModel> sorter;

    public logica_ventana(ventana delegado) {
        this.delegado = delegado;

        cargarContactos(); 

        // Asignación de ActionListeners
        this.delegado.btn_add.addActionListener(this);
        this.delegado.btn_eliminar.addActionListener(this);
        this.delegado.btn_modificar.addActionListener(this);
        this.delegado.btn_limpiar.addActionListener(this);

        JPopupMenu menuContextual = new JPopupMenu();
        JMenuItem itemModificar = new JMenuItem("Modificar Contacto");
        JMenuItem itemEliminar = new JMenuItem("Eliminar Contacto");

        menuContextual.add(itemModificar);
        menuContextual.add(itemEliminar);

        itemModificar.addActionListener(e -> delegado.btn_modificar.doClick());
        itemEliminar.addActionListener(e -> delegado.btn_eliminar.doClick());

        delegado.tbl_contactos.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Obtener la fila donde se hizo clic
                int row = delegado.tbl_contactos.rowAtPoint(e.getPoint());
                
                if (row >= 0 && row < delegado.tbl_contactos.getRowCount()) {
                    delegado.tbl_contactos.setRowSelectionInterval(row, row);
                    cargarContactoEnFormulario(); 
                    
                    // Mostrar menú si es click derecho
                    if (SwingUtilities.isRightMouseButton(e)) {
                        menuContextual.show(e.getComponent(), e.getX(), e.getY());
                    }
                } else {
                    delegado.tbl_contactos.clearSelection();
                    limpiarCampos();
                }
            }
        });

        // ENTER para Agregar/Guardar 
        KeyStroke enterKey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        delegado.contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKey, "agregar");
        delegado.contentPane.getActionMap().put("agregar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delegado.btn_add.doClick();
            }
        });

        // SUPRIMIR Eliminar Contacto
        KeyStroke deleteKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        delegado.tbl_contactos.getInputMap(JComponent.WHEN_FOCUSED).put(deleteKey, "eliminar");
        delegado.tbl_contactos.getActionMap().put("eliminar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                delegado.btn_eliminar.doClick();
            }
        });

        // Deseleccionar al hacer clic
        this.delegado.contentPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                delegado.tbl_contactos.clearSelection();
                limpiarCampos();
            }
        });

        this.delegado.tbl_contactos.getParent().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) { 
                delegado.tbl_contactos.clearSelection();
                limpiarCampos();
            }
        });

        sorter = new TableRowSorter<>(delegado.modeloTabla);
        delegado.tbl_contactos.setRowSorter(sorter);

        delegado.txt_buscar.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscar(); }
            public void removeUpdate(DocumentEvent e) { buscar(); }
            public void changedUpdate(DocumentEvent e) { buscar(); }

            private void buscar() {
                String texto = delegado.txt_buscar.getText();
                if (texto.trim().length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
                }
            }
        });
    }

    private void cargarContactos() {
        try {
            contactos = new personaDAO(new persona()).leerArchivo();
            delegado.modeloTabla.setRowCount(0); // Limpia la tabla
            for (persona c : contactos) {
                Object[] fila = {
                        c.getNombre(), c.getTelefono(), c.getEmail(),
                        c.getCategoria(), c.isFavorito() ? "Sí" : "No"
                };
                delegado.modeloTabla.addRow(fila);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(delegado, "Error al cargar los contactos.");
        }
    }

    private void limpiarCampos() {
        delegado.txt_nombres.setText("");
        delegado.txt_telefono.setText("");
        delegado.txt_email.setText("");
        delegado.cmb_categoria.setSelectedIndex(0);
        delegado.chb_favorito.setSelected(false);
        delegado.tbl_contactos.clearSelection();
    }

    private void cargarContactoEnFormulario() {
        int vistaFila = delegado.tbl_contactos.getSelectedRow();
        if (vistaFila >= 0) {
            int modeloFila = delegado.tbl_contactos.convertRowIndexToModel(vistaFila);
            persona p = contactos.get(modeloFila);

            delegado.txt_nombres.setText(p.getNombre());
            delegado.txt_telefono.setText(p.getTelefono());
            delegado.txt_email.setText(p.getEmail());
            delegado.cmb_categoria.setSelectedItem(p.getCategoria());
            delegado.chb_favorito.setSelected(p.isFavorito());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == delegado.btn_limpiar) {
            limpiarCampos();
            return;
        }

        String nombres = delegado.txt_nombres.getText().trim();
        String telefono = delegado.txt_telefono.getText().trim();
        String email = delegado.txt_email.getText().trim();
        String categoria = delegado.cmb_categoria.getSelectedItem().toString();
        boolean favorito = delegado.chb_favorito.isSelected();

        // ACCIÓN: AGREGAR
        if (e.getSource() == delegado.btn_add) {
            if (nombres.isEmpty() || telefono.isEmpty() || email.isEmpty() || categoria.equals("Elija una Categoria")) {
                JOptionPane.showMessageDialog(delegado, "Todos los campos son obligatorios.");
                return;
            }

            delegado.btn_add.setEnabled(false);

            // Iniciar el hilo de carga para la barra de progreso
            Thread hiloCarga = new Thread(() -> {
                
                SwingUtilities.invokeLater(() -> {
                    delegado.prg_carga.setVisible(true);
                    delegado.prg_carga.setValue(0);
                });

                // Animar la barra (simulación de carga)
                for (int i = 0; i <= 100; i++) {
                    final int progreso = i;
                    SwingUtilities.invokeLater(() -> delegado.prg_carga.setValue(progreso));
                    
                    try {
                        Thread.sleep(10); 
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }

                SwingUtilities.invokeLater(() -> {
                    persona p = new persona(nombres, telefono, email, categoria, favorito);
                    if (new personaDAO(p).escribirArchivo()) {
                        JOptionPane.showMessageDialog(delegado, "Contacto registrado con éxito.");
                        cargarContactos();
                        limpiarCampos();
                    } else {
                        JOptionPane.showMessageDialog(delegado, "Error al registrar el contacto.");
                    }

                    delegado.prg_carga.setValue(0);
                    delegado.prg_carga.setVisible(false);
                    
                    delegado.btn_add.setEnabled(true);
                });
            });

            hiloCarga.start();
        }

        // MODIFICAR
        else if (e.getSource() == delegado.btn_modificar) {
            int vistaFila = delegado.tbl_contactos.getSelectedRow();
            if (vistaFila >= 0) {
                int modeloFila = delegado.tbl_contactos.convertRowIndexToModel(vistaFila);

                persona p = contactos.get(modeloFila);
                p.setNombre(nombres);
                p.setTelefono(telefono);
                p.setEmail(email);
                p.setCategoria(categoria);
                p.setFavorito(favorito);

                try {
                    new personaDAO(new persona()).actualizarContactos(contactos);
                    JOptionPane.showMessageDialog(delegado, "Contacto actualizado correctamente.");
                    cargarContactos();
                    limpiarCampos();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(delegado, "Error al actualizar el contacto.");
                }
            } else {
                JOptionPane.showMessageDialog(delegado, "Seleccione un contacto de la tabla para modificar.");
            }
        }

        // ELIMINAR
        else if (e.getSource() == delegado.btn_eliminar) {
            int vistaFila = delegado.tbl_contactos.getSelectedRow();
            if (vistaFila >= 0) {
                int confirmacion = JOptionPane.showConfirmDialog(delegado, "¿Seguro que desea eliminar este contacto?",
                        "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    int modeloFila = delegado.tbl_contactos.convertRowIndexToModel(vistaFila);
                    contactos.remove(modeloFila);

                    try {
                        new personaDAO(new persona()).actualizarContactos(contactos);
                        JOptionPane.showMessageDialog(delegado, "Contacto eliminado.");
                        cargarContactos();
                        limpiarCampos();
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(delegado, "Error al eliminar el contacto.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(delegado, "Seleccione un contacto de la tabla para eliminar.");
            }
        }
    }
}
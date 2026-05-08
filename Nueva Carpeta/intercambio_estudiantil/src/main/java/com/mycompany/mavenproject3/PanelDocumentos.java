package com.mycompany.mavenproject3;

import javax.swing.*;
import java.awt.*;

public class PanelDocumentos extends JPanel
{
    private Control herramientas;
    private JTabbedPane tabbedPane;

    public PanelDocumentos(Control herramientas, JTabbedPane tabbedPane)
    {
        this.herramientas = herramientas;
        this.tabbedPane = tabbedPane;

        initComponents();
    }

    private void initComponents()
    {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        JLabel titulo = new JLabel("Subir Documentos a Trámite");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titulo, gbc);

        // Convenio
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 1;

        add(new JLabel("Convenio:"), gbc);

        JComboBox<String> comboConvenioDoc = new JComboBox<>();

        gbc.gridx = 1;
        add(comboConvenioDoc, gbc);

        // Trámite
        gbc.gridx = 0;
        gbc.gridy = 2;

        add(new JLabel("ID Trámite:"), gbc);

        JComboBox<String> comboTramite = new JComboBox<>();

        gbc.gridx = 1;
        add(comboTramite, gbc);

        // Tipo documento
        gbc.gridx = 0;
        gbc.gridy = 3;

        add(new JLabel("Tipo de Documento:"), gbc);

        JComboBox<TipoDocumento> comboTipoDoc =
            new JComboBox<>(TipoDocumento.values());

        gbc.gridx = 1;
        add(comboTipoDoc, gbc);

        // Nombre archivo
        gbc.gridx = 0;
        gbc.gridy = 4;

        add(new JLabel("Nombre del Archivo:"), gbc);

        JTextField txtNombreArchivo = new JTextField(20);

        gbc.gridx = 1;
        add(txtNombreArchivo, gbc);

        // Botón seleccionar
        JButton btnSeleccionar = new JButton("Simular Selección");

        gbc.gridx = 2;
        add(btnSeleccionar, gbc);

        // Área estado
        JTextArea areaEstado = new JTextArea(8, 40);
        areaEstado.setEditable(false);
        areaEstado.setBorder(
            BorderFactory.createTitledBorder("Estado del Trámite")
        );

        JScrollPane scrollEstado = new JScrollPane(areaEstado);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;

        add(scrollEstado, gbc);

        // Botón subir
        JButton btnSubir = new JButton("Subir Documento");

        btnSubir.setBackground(new Color(255, 193, 7));
        btnSubir.setForeground(Color.BLACK);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        add(btnSubir, gbc);

        // Botón eliminar
        JButton btnEliminarDoc = new JButton("Eliminar Documento");

        btnEliminarDoc.setBackground(new Color(220, 53, 69));
        btnEliminarDoc.setForeground(Color.WHITE);

        gbc.gridx = 1;
        gbc.gridy = 7;

        add(btnEliminarDoc, gbc);

        // -------------------------
        // Actualizar combos
        // -------------------------

        actualizarComboConvenios(comboConvenioDoc);

        comboConvenioDoc.addActionListener(e -> {
            actualizarComboTramites(comboConvenioDoc, comboTramite);
        });

        comboTramite.addActionListener(e -> {
            actualizarEstadoTramite(
                comboConvenioDoc,
                comboTramite,
                areaEstado
            );
        });

        // Cambio pestaña
        tabbedPane.addChangeListener(e -> {

            if (tabbedPane.getSelectedComponent() == this)
            {
                actualizarComboConvenios(comboConvenioDoc);
            }
        });

        // Seleccionar archivo
        btnSeleccionar.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();

            if (fileChooser.showOpenDialog(this)
                == JFileChooser.APPROVE_OPTION)
            {
                txtNombreArchivo.setText(
                    fileChooser.getSelectedFile().getName()
                );
            }
        });

        // Subir documento
        btnSubir.addActionListener(e -> {

            String idConvenio =
                (String) comboConvenioDoc.getSelectedItem();

            String idTramite =
                (String) comboTramite.getSelectedItem();

            TipoDocumento tipo =
                (TipoDocumento) comboTipoDoc.getSelectedItem();

            String nombreArchivo =
                txtNombreArchivo.getText().trim();

            if (idConvenio == null
                || idTramite == null
                || nombreArchivo.isEmpty())
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Complete todos los campos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            Convenio convenio =
                herramientas.buscarConvenio(
                    idConvenio.split(" - ")[0]
                );

            if (convenio == null)
                return;

            Tramite tramite = convenio.getTramites().stream()
                .filter(t -> t.getIdTramite().equals(idTramite))
                .findFirst()
                .orElse(null);

            if (tramite == null)
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Trámite no encontrado",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );

                return;
            }

            tramite.subirDocumento(tipo, nombreArchivo);

            convenio.validarYActualizarEstado(tramite);

            actualizarEstadoTramite(
                comboConvenioDoc,
                comboTramite,
                areaEstado
            );

            JOptionPane.showMessageDialog(
                this,
                "Documento subido exitosamente",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE
            );

            txtNombreArchivo.setText("");
        });

        // Eliminar documento
        btnEliminarDoc.addActionListener(e -> {

            String idConvenio =
                (String) comboConvenioDoc.getSelectedItem();

            String idTramite =
                (String) comboTramite.getSelectedItem();

            TipoDocumento tipo =
                (TipoDocumento) comboTipoDoc.getSelectedItem();

            if (idConvenio == null
                || idTramite == null
                || tipo == null)
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Selecciona convenio, trámite y tipo de documento."
                );

                return;
            }

            String idConvenioReal =
                idConvenio.contains(" - ")
                ? idConvenio.split(" - ")[0]
                : idConvenio;

            boolean ok =
                herramientas.eliminarDocumentoDeTramite(
                    idConvenioReal,
                    idTramite,
                    tipo
                );

            if (ok)
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Documento eliminado."
                );

                actualizarEstadoTramite(
                    comboConvenioDoc,
                    comboTramite,
                    areaEstado
                );
            }
            else
            {
                JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar el documento."
                );
            }
        });
    }

    // =========================
    // MÉTODOS AUXILIARES
    // =========================

    private void actualizarComboConvenios(JComboBox<String> combo)
    {
        combo.removeAllItems();

        for (Convenio c : herramientas.getConvenios())
        {
            combo.addItem(
                c.getIdConvenio() + " - " + c.getNombre()
            );
        }
    }

    private void actualizarComboTramites(
        JComboBox<String> comboConvenio,
        JComboBox<String> comboTramite)
    {
        comboTramite.removeAllItems();

        String idConvenio =
            (String) comboConvenio.getSelectedItem();

        if (idConvenio != null)
        {
            Convenio convenio =
                herramientas.buscarConvenio(
                    idConvenio.split(" - ")[0]
                );

            if (convenio != null)
            {
                for (Tramite t : convenio.getTramites())
                {
                    comboTramite.addItem(t.getIdTramite());
                }
            }
        }
    }

    private void actualizarEstadoTramite(
        JComboBox<String> comboConvenio,
        JComboBox<String> comboTramite,
        JTextArea areaEstado)
    {
        String idConvenio =
            (String) comboConvenio.getSelectedItem();

        String idTramite =
            (String) comboTramite.getSelectedItem();

        if (idConvenio != null && idTramite != null)
        {
            Convenio convenio =
                herramientas.buscarConvenio(
                    idConvenio.split(" - ")[0]
                );

            if (convenio != null)
            {
                Tramite tramite = convenio.getTramites().stream()
                    .filter(t -> t.getIdTramite().equals(idTramite))
                    .findFirst()
                    .orElse(null);

                if (tramite != null)
                {
                    areaEstado.setText(
                        "Estado: "
                        + tramite.getEstado()
                    );
                }
            }
        }
    }
}

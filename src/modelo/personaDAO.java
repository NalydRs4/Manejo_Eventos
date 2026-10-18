package modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class personaDAO {
    private File archivo;
    private persona persona;

    public personaDAO(persona persona) {
        this.persona = persona;
        // Exportación de contactos a un archivo CSV 
        File directorio = new File("src/modelo", "gestionContactos");
        if (!directorio.exists()) {
            directorio.mkdir();
        }
        archivo = new File(directorio, "datosContactos.csv");
        prepararArchivo();
    }

    private void prepararArchivo() {
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean escribirArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, true))) {
            writer.write(persona.datosContacto());
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<persona> leerArchivo() throws IOException {
        List<persona> personas = new ArrayList<>();
        if (!archivo.exists() || archivo.length() == 0) return personas;

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length == 5) {
                    persona p = new persona(
                        datos[0], datos[1], datos[2], datos[3], Boolean.parseBoolean(datos[4])
                    );
                    personas.add(p);
                }
            }
        }
        return personas;
    }

    public void actualizarContactos(List<persona> personas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivo, false))) {
            for (persona p : personas) {
                writer.write(p.datosContacto());
                writer.newLine();
            }
        }
    }
}
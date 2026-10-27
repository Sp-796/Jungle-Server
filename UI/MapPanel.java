package UI; // Define el paquete al que debe pertenecer
import javax.swing.JPanel; // Importa un lienzo o panel en blanco
import java.awt.Graphics; // Importa las herramientas basicas para realizar los dibujos
import java.awt.Graphics2D; // Importa herramientas avanzadas de dibujo facilitando creaciones mas complejas
import java.awt.Color; // Importa la seleccion de multiples colores por usar en el lienzo
import Client.Model.GameState; // Importa la plantilla necesaria para actualizar el estado del juego
import Client.Model.Animal; // Importa a los posibles animales que pueden ser dibujados
import Client.Model.Zone; // Importa a los posibles mapas o fondos que pueden ser dibujados
import java.util.Map; // Importa la libreria de Java para facilitar el manejo de diccionarios (reglas basicas)
import java.util.HashMap; // Importa la libreria de Java para facilitar el manejo de diccionarios (manejo de diccionarios)
import java.awt.Image; // Importa el molde abstracto para las imagenes siendo funcional para su manipulacion o dibujo en la pantalla
import javax.swing.ImageIcon; // Importa una herramienta de busqueda del archivo que contiene la imagen en el dispositivo

public class MapPanel extends JPanel { // Hereda de JPanel sus atributos y metodos para crear el lienzo en blanco 
    
    private GameState currentGameState; // Objeto que almacena la actualizacion mas reciente de la partida
    private Map<String, Image> imageCache; // Diccionario utilizado para almacenar las imagenes cargadas

    public MapPanel() { // Constructor del cuadro visual o lienzo por crear

        this.setBackground(new Color(34, 139, 24)); // Brinda al lienzo un color por defecto siendo una tonalidad de verdes
        this.imageCache = new HashMap<>(); // Crea el almacen en la memoria de la computadora para las imagenes usadas recientemente

    }

    public void updateMap(GameState gameState) { // Metodo que sera llamado por MainWindow cada vez que se obtengan datos nuevos

        this.currentGameState = gameState; // Actualiza la informacion con los respectivos nuevos valores
        this.repaint(); // Una vez que los datos cambian se le indica a Java que debe repintar en el lienzo
    }

    private Image getAnimalImage(String animalName) { // Metodo de cache que busca la imagen y en caso de no tenerla genera la carga respectiva y sino se recicla

        if (!imageCache.containsKey(animalName)) { // Valida si aun no se tiene la imagen en el diccionario

            String imagePath = "agregar ruta de las imagenes" + animalName + ".png"; // Agrega la imagen segun el estandar que es nombre de la imagen + el tipo de imagen que es png
            Image newImage = new ImageIcon(imagePath).getImage(); // Carga la imagen desde el archivo respectivo
            imageCache.put(animalName, newImage); // Guarda la imagen en el diccionario para usarla en el futuro

        }

        return imageCache.get(animalName); // En caso de existir la imagen en el diccionario se retorna la imagen de la misma

    }

    @Override // Permite la sobreescritura del metodo original de JPanel mediante polimorfismo
    protected void paintComponent(Graphics g) { // Metodo que permite modificar las instrucciones de dibujo del JPanel

        super.paintComponent(g); // Sobre la clase padre se debe de borrar el lienzo anterior y evitar el contenido basura al crear el nuevo
        if (currentGameState == null) { // Valida si se ha recibido nueva informacion que indique la modificacion del lienzo

            return; // No retorna valor o instruccion alguna, evitando errores fantasmas

        }

        Graphics2D g2d = (Graphics2D) g; // Permite la conversion de un pincel basico a uno mas profesional

        //PENDIENTE: colocar las imagenes del juego

        if (currentGameState.getVisibleAnimals() != null) { // Valida que la lista de animales visibles actualmente no se encuentre vacia

            for (Animal animal : currentGameState.getVisibleAnimals()) { // Se recorre la lista de animales visibles actualmente

                Image animalImg = getAnimalImage(animal.getSpecies()); // Se pide la imagen del animal al metodo creado previamente
                g2d.drawImage(animalImg, (int)animal.getX(), (int)animal.getY(), 40, 40, null); // Dibuja el animal con 40 de ancho y alto y usa null dado que la imagen esta lista para usarse
                g2d.setColor(Color.white); // Coloca el pincel con el color blanco antes de realizar otro dibujo
                g2d.drawString(animal.getSpecies(), (int)animal.getX(), (int)animal.getY() - 5); // Coloca el nombre del animal centrandolo con X y Y, la resta de 5 al Y es un truco visual para que no tape la cara del animal

            }

        }
    }
}

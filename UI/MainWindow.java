package UI; // Define el paquete al que debe pertenecer
import javax.swing.JFrame; // Importa la libreria para el manejo de ventanas y botones
import javax.swing.JPanel; // Importa la clase apra crear lienzos rectangulares
import java.awt.CardLayout; // Importa la libreria que permite o facilita el control del diseño y dimensiones
import Client.Model.GameState; // Importa la clase requerida para validar el observer
import Constants.IConstants; // Importa la clase que contiene las constantes definidas desde el comienzo del codigo
import java.awt.BorderLayout; // Importa el administrador de dieño por puntos cardinales, incluyendo el centro

public class MainWindow extends JFrame implements Observer {
    
    private MapPanel mapPanel; // Referencia al panel que dibuja el terreno y los animales
    private HUDPanel hudPanel; // Referencia al panel superior de la pantalla que muestra las estadisticas
    private JPanel gameScreen; // Panel contenedor que actua como puente principal entre el mapa y el HUD
    private CardLayout cardLayout; // Administrador de diseño que permite tener varias pantallas y mostrar solo una a la vez (funciona como baraja de cartas)

    public MainWindow() { // Constructor de la ventana principal del juego

        this.setTitle("Biodiversidad CR - La Batalla del Bosque"); // Define el titulo de la barra superior de la ventana
        this.setSize(IConstants.windowWidth, IConstants.windowHeight); // Define el tamaño de la ventana a partir de las constantes
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Establece el cierre de la ventana cuando se presione la X de esta
        this.setResizable(false); // Define que el usuario no pueda cambiar el tamaño de la ventana
        this.setLocationRelativeTo(null); // Establece que Java deba calcular el centro de la pantalla y colocar ahi la ventana
        this.cardLayout = new CardLayout(); // Configura el diseño de una nueva ventana segun la baraja de cartas
        this.setLayout(cardLayout); // Implementa una sola ventana principal a la cual se le aplican las reglas como baraja de cartas poniendo una sobre la otra

        mapPanel = new MapPanel(); // Objeto que construye el lienzo de dibujo grafico
        hudPanel = new HUDPanel(); // Objeto que consturye la barra superior que posee las estadisticas del juego segun el jugador
        gameScreen = new JPanel(); // Facilita la construccion del contenedor padre para la pantalla de juego
        gameScreen.setLayout(new BorderLayout()); // Configura el diseño de bordes para facilitar el manejo de cosas arriba y en el centro
        gameScreen.add(hudPanel, BorderLayout.NORTH); // Coloca la barra de estadisticas en la parte superior del contenedor
        gameScreen.add(mapPanel, BorderLayout.CENTER); // Coloca el lienzo del mapa en el centro de modo que abarque el espacio sobrante
        this.add(gameScreen, "VentanaDeJuego"); // Inserta el contenedor padre al manejo de ventanas con el nombre indicado

    }

    @Override // Aplicacion de polimorfismo en la que se le indica la sobreescritura de un metodo heredado de otra clase
    public void update(GameState gameState) { // Metodo que reacciona cada vez que el servidor notifique un cambio en la partida

        if (gameState != null) { // Valida que los datos recibidos no se encuentren vacios

            hudPanel.updateHUD(gameState); // Inyecta los nuevos datos a las etiquetas del panel superior
            mapPanel.updateMap(gameState); // Inyecta las nuevas posiciones de los animales y zonas en el mapa para que aliste el respectivo dibujo 

        }

        this.repaint(); // Establece lo que debe ser repintado en la ventana al sufrir cambios en ciertos datos o informacion

    }

    public void showPanel(String panelName) { // Metodo que facilita la navegacion entre distintas pantallas

        cardLayout.show(this.getContentPane(), panelName); // Establece la ventana del juego que debe ser mostrada a continuacion en la respectiva pantalla

    }
}

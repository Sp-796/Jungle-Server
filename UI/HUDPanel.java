package UI; // Define el paquete al que debe pertenecer
import javax.swing.JPanel; // Importa un lienzo o panel en blanco
import javax.swing.JLabel; // Importa la libreria necesaria para la impresion de textos fijos en pantalla
import java.awt.Color; // Importa la seleccion de multiples colores por usar en el lienzo
import java.awt.Font; // Importa la libreria correspondiente al estilo de la letra
import java.awt.FlowLayout; // Importa la libreria asociada a una herramienta de acomodo automatizado de los componentes visuales
import Client.Model.GameState; // Importa el modelo del estado de juego

public class HUDPanel extends JPanel { // Clase cuya finalidad es mostrar en tiempo real los datos del jugador

    private JLabel lblPlayerName; // Crea la etiqueta para mostrar el nombre del jugador o nickname
    private JLabel lblAliveAnimals; // Etqiqueta para mostrar la cantidad de animales vivos
    private JLabel lblCoffeeCups; // Etiqueta que muestra la cantidad de tazas de cafe consumidas
    private JLabel lblBotsKilled; // Etiqueta que muestra la cantidad de bots eliminados por el jugador

    public HUDPanel() { // Constructor que permite inicializar el HUDPanel con los datos

        this.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10)); // Configura el diseño del flujo horizontal para alinear cada uno de los elementos en la barra
        this.setBackground(new Color(45, 45, 45)); // Configuracion del color de fondo de la barra de estado
        
        lblPlayerName = new JLabel("Nickname: ---"); // Inicializa las etiquetas con el texto por default
        lblAliveAnimals = new JLabel("Animales vivos: 0"); // Inicializa el valor total de animales con vida, es decir, las vidas en el juego
        lblCoffeeCups = new JLabel("Cafe consumido: 0"); // Inicializa el valor de las tazas de cafe consumidas
        lblBotsKilled = new JLabel("Bots eliminados: 0"); // Inicializa la cantidad de bots que ha eliminado el jugador

        Font hudFont = new Font("Arial", Font.BOLD, 14); // Define el estilo de la letra por mostrar en pantalla
        Color textColor = Color.WHITE; // Brinda el color al texto segun lo deseado

        styleLabel(lblPlayerName, hudFont, textColor); // Permite estilizar la etiqueta deseada con sus caracteristicas
        styleLabel(lblAliveAnimals, hudFont, textColor); // Permite estilizar la etiqueta deseada con sus caracteristicas
        styleLabel(lblCoffeeCups, hudFont, textColor); // Permite estilizar la etiqueta deseada con sus caracteristicas
        styleLabel(lblBotsKilled, hudFont, textColor); // Permite estilizar la etiqueta deseada con sus caracteristicas

        this.add(lblPlayerName); // Agrega el componente al panel para que este sea visible
        this.add(lblAliveAnimals); // Agrega el componente al panel para que este sea visible
        this.add(lblCoffeeCups); // Agrega el componente al panel para que este sea visible
        this.add(lblBotsKilled); // Agrega el componente al panel para que este sea visible

    }

    private void styleLabel(JLabel label, Font font, Color color) { // Metodo auxiliar que evita la repeticion generica de codigo

        label.setFont(font); // Aplica el estilo de letra que se ha entregado como parametro
        label.setForeground(color); // Aplica el color del texto en contraste con el fondo de este

    }

    public void updateHUD(GameState gameState) { // Metodo que se llama desde MainWindow cada vez que el servidor actualice el estado del juego

        if (gameState == null) { // Revision en caso de que no haya nada por actualizar segun el servidor

            return; // Al no existir llamada entonces solamente se sale sin retornar valor alguno

        }

        lblPlayerName.setText("Nickname: " + gameState.getPlayerName()); // Actuliza el texto en pantalla mostrado en las respectivas etiquetas extrayendo los datos
        lblAliveAnimals.setText("Animales vivos: " + gameState.getAliveAnimalsPlayer()); // Actuliza el texto en pantalla mostrado en las respectivas etiquetas extrayendo los datos
        lblCoffeeCups.setText("Cafe consumido: " + gameState.getCoffeeCupsCollected()); // Actuliza el texto en pantalla mostrado en las respectivas etiquetas extrayendo los datos
        lblBotsKilled.setText("Bots eliminados: " + gameState.getBotsKilled()); // Actuliza el texto en pantalla mostrado en las respectivas etiquetas extrayendo los datos

    }

}
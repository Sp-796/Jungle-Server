package UI; // Define el paquete al que debe pertenecer
import Client.Model.GameState; // Importa el estado del juego que fue creado en el modelo

public interface Observer { // Define las reglas o plantilla que otras clases deberan de implementar si hacen uso de esta interfaz
    
    void update(GameState gameState); // Prepara el metodo para que las ventanas siempre esten listas para recibir informacion del juego

}

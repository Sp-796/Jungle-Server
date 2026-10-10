**Ideas descriptivas - Caso 2 POO**



**Animales:**



&#x09;perezoso

&#x09;mono

&#x09;tucan

&#x09;rana roja

&#x09;jaguar

&#x09;danta

&#x09;boa constructora



**Mapa de juego:**



&#x09;Bosque despejado

&#x09;Bosque con catarata (o rio)

&#x09;Sendero rocoso

&#x09;Zona montañosa

&#x09;Bosque nuboso

&#x09;Bosque seco tropical

&#x09;Manglar

&#x09;Bosque húmedo

&#x09;Paramo

&#x09;Bosque con cavernas

&#x09;Bosque profundo (llevo de especies y salvaje)

&#x09;Bosque de robledales de altura (Cordillera de Talamanca)



**Tazas de cafe:**



&#x09;10 en total



**Bandera:**



&#x09;Equipo A = guaria morada

&#x09;Equipo B = yigüirro



**Manadas:**



&#x09;Jugador = 10 miembros

&#x09;Bot = 4 miembros



**Jugadores:**



&#x09;A = elige 1 animal

&#x09;B = elige 1 animal



**Rutas (caminos y conexiones):**



&#x09;1. Nivel Costero y Tierras Bajas (anillo inferior)



&#x09;	Manglar: Conecta con Bosque seco tropical y Bosque profundo.



&#x09;	Bosque seco tropical: Conecta con Manglar, Bosque despejado y Sendero rocoso.



&#x09;	Bosque profundo: Conecta con Manglar, Bosque húmedo y Bosque con cavernas.



&#x09;2. Nivel Intermedio y Transición (núcleo del mapa)



&#x09;	Bosque despejado: Conecta con Bosque seco tropical, Bosque húmedo y Bosque con catarata.



&#x09;	Bosque húmedo: Conecta con Bosque despejado, Bosque profundo y Bosque con catarata.



&#x09;	Bosque con catarata: Conecta con Bosque despejado, Bosque húmedo, Bosque con cavernas y Zona montañosa.



&#x09;	Bosque con cavernas: Conecta con Bosque profundo, Bosque con catarata y Bosque nuboso.



&#x09;3. Nivel de Altura (anillo superior)



&#x09;	Sendero rocoso: Conecta con Bosque seco tropical y Zona montañosa.



&#x09;	Zona montañosa: Conecta con Sendero rocoso, Bosque con catarata, Bosque nuboso y Bosque de robledales de altura.



&#x09;	Bosque nuboso: Conecta con Bosque con cavernas, Zona montañosa y Paramo.



&#x09;	Bosque de robledales de altura: Conecta con Zona montañosa y Paramo.



&#x09;	Paramo: Conecta únicamente con Bosque nuboso y Bosque de robledales de altura.



**Partida:**



&#x09;Ubicacion = sala o partida

&#x09;Codigo = 4 caracteres alfanuméricos (generado por el servidor)

&#x09;Estado = espera, iniciado, finalizado



**Atributos:**



&#x09;Energia = jugador (20) y bot (10)

&#x09;Energia maxima = 20

&#x09;Daño manada = jugador (2) y bot (1)

&#x09;Tiempo de ataque = 1 ataque / seg.



**Visibilidad (mapa):**



&#x09;Animales en zona = true / false

&#x09;Visualizacion = 1 zona



**Gestor manadas:**



&#x09;Bot = 5 packs (5 animales restantes)

&#x09;Curacion = 2 bots muertos / +1 de energía a manada de jugador

&#x09;Ataque = 1 / manada

&#x09;Aparición = aleatoria (distribuida entre todos los mapas)

&#x09;Control ataque = no se atacan entre bots





&#x09;


Lección #2: Patrones de diseño, Servidor de aplicación Integración de servicios.
Vamos a crear un servidor de aplicación sencillo con Java y las tecnologías que hemos visto en clase. Lo integraremos con un API de AI (servicio gratuito de grok o deepseek) para dos casos específicos.
Lo integraremos con Telegram para crear un compañero de grupo que basado en una conversación cada 3 o 4 mensajes, interprete el contexto y genere un chiste.
Luego Crearemos la funcionalidad para que pueda leer una conversación entre un cliente y un vendedor (ustedes seleccionan la industria) y por medio de otro chat, aconseje al vendedor para conseguir diferentes objetivos: Rechazar una devolución, Motivar lo a que compre X producto, Sugerir que haga un cambio de producto por uno mejor(upselling).

Continuaremos aplicando las mejores técnicas de programación y aplicaremos los siguientes patrones de diseño:
Adapter
Para encapsular el acceso a servicios externos (Telegram API, API de IA) y desacoplarlos de la lógica interna.

Strategy
Para cambiar dinámicamente la lógica de respuesta según el objetivo (rechazar devolución, upselling, motivar la compra).

Singleton
Para manejar la configuración global del servidor, como la conexión con la API de IA o la configuración del bot de Telegram.

Debe realizar una investigación pequeña sobre que es un patrón de diseño, y en especifico estos 3, luego como aplicarlos, asegúrese de discutirlos con sus compañeros de grupo.

No se olvide de dockerizar su aplicación y mejorar el readme de GitHub con https://readme.so/es 
Notas finales
No se requiere autenticación para este ejercicio.
No se requiere base de datos para este trabajo.
Puede utilizar chat gpt, claude y gemini en distintas etapas del ejercicio a fin de poder realizar todo el trabajo sin pagar.
Su trabajo debe estar en un repositorio de GitHub,
Este trabajo está pensado para realizarse en una sesión de 2 a 3 horas, en grupos de 3 personas.
Comparta su trabajo en un correo electrónico con el profesor, tome screenshots, describa los pasos realizados, los hallazgos encontrados y el aprendizaje realizado.

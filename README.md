# Ejecucuin

- Estando en `Launcher.java` pulsar el boton de play, si no se tiene la base de datos te saldra el mensaje de error de conexion a la base de datos lo cual no permitira agregar usuarios

# Creacion de la base de datos

## Usamos estos comandos en la terminal de docker para que el usuario adat tenga permisos en caso de que de problemas con los permisos

- Para levantar el docker hacer un ``docker compose up -d`` en la carpeta que esta el ``compose.yaml``

- En caso de querer borrar el docker junto al contenido hacer ``docker compose down -v``


## Si no tienes acceso a la tabla / DB usar los siguientes comandos
- `docker exec -it mariadb-adat bash` 
- `mariadb -u root -p` 
- despues introducimos la contraseña de root,
- `GRANT ALL PRIVILEGES ON TableViewDB.* TO 'admin'@'%';` 
- para dar permisos a usuario adat.


Nos conectamos a la base de datos con cualquier gestor de bases de datos, en mi caso, dbeaver y creamos la tabla con una sentencia sql


Las credenciales estaran a la altura de .env.example y db.properties.example,
En su respectivo .env y db.properties.example
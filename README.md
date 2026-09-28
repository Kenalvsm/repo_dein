# Creacion de la base de datos

## Usamos estos comandos en la terminal de docker para que el usuario adat tenga permisos 

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
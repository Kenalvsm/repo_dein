usamos estos comandos en la terminal de docker para que el usuario adat tenga permisos 
`docker exec -it mariadb-adat bash`, `mariadb -u root -p` y despues introducimos la contraseña de root,
`GRANT ALL PRIVILEGES ON TableViewDB.* TO 'admin'@'%';` para dar permisos a usuario adat.


las credenciales estaran a la altura de .env.example y db.properties.example en su respectivo .env y db.properties.example
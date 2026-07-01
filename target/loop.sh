while true
do
	java -Xms1G -Xmx1G -jar minecraft-backend-1.0.0.jar
        echo 'If you don't like to restart this server, you can make STRG + C
	echo "Rebooting in:"
	for i in 5 4 3 2 1
	do
		echo "$i..."
		sleep 1
	done
	echo "Serverrestart"
done

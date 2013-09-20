# Integration-test-suite.sh
# This file will be used to kick off all integration tests

#initialize constants
JAR_PATH=../../../../Dropbox.jar
OUTPUT=output.txt
HOST=./host-dir
CLIENT=./client-dir

if [ $# -gt 1 ]
then
	echo "usage: 'bash integration-test-suite [acceptable-wait-time]'"
	echo "default wait time is 15 seconds"
	exit
fi

#set the wait time
if [[ -z $1 ]]; then WAIT=15; else WAIT=$1; fi

if [ -e $JAR_PATH ]
then	
	#setup
	echo "Setting up..."
	set -e # set the error flag, so that a non-zero exit status will end the script
	mkdir $HOST
	mkdir $CLIENT
	if [ -e $OUTPUT ]
	  then rm $OUTPUT
	fi
	echo "ERRORS:" > $OUTPUT
	
	#define cleanup
	function cleanup {
		echo
		echo "cleaning up and exiting"
		trap 'kill $(jobs -p)' EXIT
		if [ -e res-host-dir ]; then rm -r res-host-dir; fi
		if [ -e res-client-dir ]; then rm -r res-client-dir; fi
		mv host-dir res-host-dir
		mv client-dir res-client-dir
	}
	
	#define check_error
	function check_error {
		ERR=$1
		if [ ! $ERR -eq 0 ]; then cleanup; exit 1; fi  
	}

	# start the Dropbox host
	echo "Starting the host..."
	java -jar $JAR_PATH $HOST &> host$OUTPUT &
	sleep 1
	# verify the host
	hostcounter=0
	while [[ -z $(cat host$OUTPUT | grep STARTED) ]]
	do
		sleep 1
		hostcounter=$(($hostcounter + 1))
		if [ $hostcounter -gt $WAIT ]; then echo "FAILED to start host"; cleanup; exit 1; fi
	done

	# start the Dropbox client
	echo "Starting the client..."
	java -jar $JAR_PATH $CLIENT $(ipconfig getifaddr en1) &> client$OUTPUT &
	sleep 1
	# verify the client
	clientcounter=0
	while [[ -z $(cat client$OUTPUT | grep STARTED) ]]
	do
		sleep 1
		clientcounter=$(($clientcounter + 1))
		if [ $clientcounter -gt $WAIT ]; then echo "FAILED to start client"; cleanup; exit 1; fi
	done

	#debug
	echo "Client and Server are running. Beginning testing..."
	echo
		#echo "testing"
		#touch $HOST/host.txt
		#sleep $WAIT
		#if [ -e $CLIENT/host.txt ]
		#then echo "worked"
		#else echo "failed"
		#fi

	# test adding files
	echo "Testing the ability to add files"
	bash test-add-files.sh $OUTPUT $WAIT $CLIENT $HOST
	check_error $?

	# test updating files
	echo "Testing the ability to update files"
	bash test-update-files.sh $OUTPUT $WAIT $CLIENT $HOST
	check_error $?

	# test removing files
	echo "Testing the ability to remove files"
	bash test-delete-files.sh $OUTPUT $WAIT $CLIENT $HOST
	check_error $?

	# test all functionality
	echo "Rapidly testing add-update-remove"
	bash test-all.sh $OUTPUT $WAIT $CLIENT $HOST
	check_error $?
	
	# clear all directories
	echo "clearing "$HOST" directory"
	rm $HOST/*
	sleep $WAIT
	if [ ! -z $(ls $CLIENT/) ]; then echo "FAILED to remove all files from client" >> $OUTPUT; fi
	
	# Start another host
	echo "Trying to start another host..."
	java -jar $JAR_PATH $HOST &> host$OUTPUT &
	sleep 1
	# verify the host
	hostcounter=0
	while [[ -z $(cat host$OUTPUT | grep STARTED) ]]
	do
		sleep 1
		hostcounter=$(($hostcounter + 1))
	done
	if [ $hostcounter -lt $WAIT ]; then echo "FAILED: Allowed another host to start" >> $OUTPUT; fi

	#display output
	echo
	echo "Finished"
	echo
	cat output.txt

	#cleanup
	cleanup
else
	echo "Please create a Dropbox.jar file with the contents of the Asgn3 project, and save it in the Asgn3/ directory."
fi

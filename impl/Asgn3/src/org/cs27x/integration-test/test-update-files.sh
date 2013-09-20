# test-update-files.sh
# This file will test the ability of Dropbox to propperly update files

#setup constants
OUTPUT=$1
WAIT=$2
CLIENT=$3
HOST=$4
STOP_FLAG=0 #indicate to loops to exit
set -e

#define helper functions to minimize code duplication
function verify-updated-file { # @params,  1:expected file, 2:updated file 3:echo note, 4:print echo note?
	if [ $STOP_FLAG -eq 0 ]
	then
		if [ $4 -eq 1 ];then echo $3; fi #print an echo note, if explicitly told to
		counter=0
		while [[ ! -e $1 || ! -z $(diff $1 $2) ]] # While the file doesn't exist or the contents of the files are different
		do 
		  sleep 1
		  if [ $counter -gt $WAIT ]; then echo "FAILED "$3": "$1 >> $OUTPUT; STOP_FLAG=1; break; fi
		  counter=$(($counter+1))
		done
	fi
}

# update 1 file in the host
echo "update 1" >> $HOST/a.txt
verify-updated-file $CLIENT/a.txt $HOST/a.txt "updating a file in the host" 1
STOP_FLAG=0

# update 1 file in the client
echo "update 2" >> $CLIENT/b.txt
verify-updated-file $HOST/b.txt $CLIENT/b.txt "updating a file in the client" 1
STOP_FLAG=0

# rename 1 file in the host
mv $HOST/a.txt $HOST/foo.txt
verify-updated-file $CLIENT/foo.txt $HOST/foo.txt "re-naming a file in the host" 1
STOP_FLAG=0

# rename 1 file in the client
mv $CLIENT/b.txt $CLIENT/bar.txt
verify-updated-file $HOST/bar.txt $CLIENT/bar.txt "re-naming a file in the client" 1
STOP_FLAG=0

# update 1000 files in host
echo "updating 1000 files in the host"
for i in {1..1000}; do echo "update 3" >> $HOST/$i.txt; done
for i in {1..1000}; do verify-updated-file $CLIENT/$i.txt $HOST/$i.txt "updating 1000 files in the host" 0; done
STOP_FLAG=0

# update 1000 files in the client
echo "updating 1000 files in the client"
for i in {1001..2000}; do echo "update 4" >> $CLIENT/$i.txt; done
for i in {1001..2000}; do verify-updated-file $HOST/$i.txt $CLIENT/$i.txt "updating 1000 files in the client" 0; done
STOP_FLAG=0

# update 500 files in both, simultaneously
echo "updating 500 files in both, simultaneously"
echo "worked" > master.txt
for i in {2001..2500}; do echo "worked" > $HOST/$i-a.txt; echo "failed" > $CLIENT/$i-a.txt; done
for i in {2001..2500}; do
  verify-updated-file $CLIENT/$i-a.txt master.txt "updating 500 files to the both, simultaneously" 0
  verify-updated-file $HOST/$i-a.txt master.txt "updating 500 files to the both, simultaneously" 0
done
rm master.txt
STOP_FLAG=0
 
echo

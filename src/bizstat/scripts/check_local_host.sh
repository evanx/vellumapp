
echo $0 $@ `date +%T`

echo_loadavg() {
  cat /proc/loadavg | cut -d. -f1
}

echo_diskmax() {
  df | grep "% /" | sed -s 's/.* \([0-9][0-9]*\)%/\1/' | sort -nr | head -1 | cut -f1 -d' '
}

check_ok_url() {
  echo "check HTTP $1"
  curl -I -s $1 | grep "HTTP/1.1 200 OK"
}

check_load() {
  if [ `echo_loadavg` -gt 1 ]
  then
    exit 1
  fi
}

check_disk() {
  if [ `echo_diskmax` -gt 80 ]
  then
    exit 1
  fi
}

warn_not_ok() {
  if ! check_ok_$1
  then
    echo "WARNING $1 not ok" 
    exit 1
  fi
}

check_lesser() {
  echo "check $1 `echo_$1` less than $2"
  [ `echo_$1` -lt $2 ]
}

set -e 

check_lesser loadavg 4

check_lesser diskmax 70

check_ok_url https://qamps.bizswitch.net:9443/retailAdmin/logon.jsp

exit 0

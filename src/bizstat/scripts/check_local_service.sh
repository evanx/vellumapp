
date +%T
echo $0 $@

echo_loadavg() {
  cat /proc/loadavg | cut -d. -f1
}

echo_diskmax() {
  df | grep "% /" | sed -s 's/.* \([0-9][0-9]*\)%/\1/' | sort -nr | head -1 | cut -f1 -d' '
}

check_QampsElec() {
  exit 2
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

check_$2

exit 0

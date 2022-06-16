pwd=$(pwd)
if [ ! -e pom.xml ]
then
  if [ -e ../pom.xml ]
  then
    cd ..
  fi
fi
if [ ! -e pom.xml ]
then
  echo Bad directory
  cd $pwd
  exit 0
fi
mvn clean
mvn compile
mvn package
mkdir -p output/file-rotate
cp shell/file-rotate.sh output/file-rotate
cp shell/file-rotate.cmd output/file-rotate
cp target/file-rotate.jar output/file-rotate
cd output
7z a -mx9 file-rotate.7z file-rotate
cd ..
rm output/file-rotate/file-rotate.sh
rm output/file-rotate/file-rotate.cmd
rm output/file-rotate/file-rotate.jar
rmdir output/file-rotate
if [ -d download ]
then
  cp output/file-rotate.7z download/
  rm output/file-rotate.7z
  rmdir output
fi
cd $pwd

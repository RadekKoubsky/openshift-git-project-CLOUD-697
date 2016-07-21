# openshift-git-project-CLOUD-697

This is a reproducer for CLOUD-697.

##Setup:

1) Create an OpenShift project with template

oc new-project "your project name"
oc create -f processserver-app-secret.json
oc create -f jboss-processserver63-openshift.json
oc create -f processserver63-amq-postgresql-persistent-s2i.json
oc import-image jboss-processserver63-openshift

or you can use the /create_project.sh file

2) Create an application from the processserver63-amq-postgresql-persistent-s2i template
e.g. using web console

3) Run the client and see logs

go to /cloud-697-client
run mvn -q exec:java -Dexec.mainClass=com.redhat.xpaas.qe.Client -Dexec.args="the hostname of the openwire-sec-route route e.g. openwire-sec-route-cloud-697.apps.latest.xpaas"

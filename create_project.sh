#!/bin/sh

oc new-project cloud-697-reproducer
oc create -f processserver-app-secret.json
oc create -f jboss-processserver63-openshift.json
oc create -f processserver63-amq-postgresql-persistent-s2i.json
oc import-image jboss-processserver63-openshift

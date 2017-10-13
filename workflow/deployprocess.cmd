@echo on

#mvn clean deploy
call curl -u admin:password1! -H "Content-Type: application/xml" -X POST -d @deploy-descriptor.xml http://localhost:8080/kie-wb/rest/deployment/com.hatchworks.workflow:workflow:0.0.1-SNAPSHOT/undeploy

call curl -u admin:password1! -H "Content-Type: application/xml" -X POST -d @deploy-descriptor.xml http://localhost:8080/kie-wb/rest/deployment/com.hatchworks.workflow:workflow:0.0.1-SNAPSHOT/deploy

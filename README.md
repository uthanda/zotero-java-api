# zotero-java-api
 Java Implementaton of a Zotero API

This is a Java-based client library for the Zotero.org web api.  The goal is to provide a feature-complete API that can manage all elements of Zotero documents, items, attachments, collections, etc.

The project is divided into two modules: the **zotero-java-api-core** and **zotero-java-api-samples**.  The core is the primary API and the samples are usage sample files.  Please note, these are not yet 100% complete.

To use the API, you will need to create an API key from your account (from this page: https://www.zotero.org/settings/keys/new).  You then create a new Library using the *zotero.api.Library.createLibrary()* method.  With the created Library object, you can fetch and create items and collections.

Right now, you will need to download the code and compile it in order to make use of it.  The longer-term goal is to register a Maven space and deploy as a proper Maven artifact, but we're not there yet.

We are looking for additional contributors who would be interested in helping complete this work.

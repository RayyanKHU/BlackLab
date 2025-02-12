# BlackLab Reverse Proxy Server

A reverse proxy between the client and a BlackLab backend (either BLS or Solr). 

One goal of this is API compatibility, to offer one API regardless of whether you're using (an older version of) BLS or Solr+BlackLab.

Another goal is security: it is generally not recommended to open the Solr port directly to the internet. This provides a layer of security, enabling only the BlackLab functionality and shielding any other Solr functionality (and with it, potential security issues).

## TODO

This is a work in progress. Still to do:

- anything related to creating and adding private user corpora
- `/cache-info`
- ensure proxying to BlackLab Server works as well

## API versions (v3 / v4)

The Solr component and this proxy introduce version 4.0 of the BlackLab webservice protocol (API version numbers match the BlackLab version they were released with)

Changes:
- New key added to server info page (`/`): `apiVersion` (valid values: `3.0` and `4.0`; assume `3.0` if missing)
- Two keys were renamed on the corpus info page (`/CORPUSNAME`) to be more consistent: in the `versionInfo` block, 
  `blacklabVersion` and `blacklabBuildTime` are now spelled with a lowercase `l`, just like on the server info page. 
- The `/hits` and `/docs` responses don't include the `docFields` and `metadataFieldDisplayNames` keys anymore.
  This information can be found on the corpus info page (`/CORPUSNAME`) and need not be sent with each hit request.
- Similarly, the document info page (`/docs/DOC_PID`) no longer includes `docFields`, `metadataFieldDisplayNames` or 
  `metadataFieldGroups` by default.

For all of these changes, you can add `api=3` to produce the old behaviour. You can also specify 
`parameters.api=3` in `blacklab-server.yaml` (or `blacklab-webservice.yaml` for Solr).
This is meant as a transitional measure, and v3 compatibility will eventually be removed.

## Configuration

Create a configuration file named `proxy.yaml` in one of these locations: `$HOME/.blacklab/`, `/etc/blacklab/`, with 
the following structure (for proxying to Solr+BlackLab):

```yaml
proxyTarget:
  url: http://localhost:8983/solr
  protocol: solr
  defaultCorpusName: test      # needed for "server-wide" BLS operations (Solr request always needs a core)
```

To proxy to a BlackLab Server instance instead:

```yaml
proxyTarget:
  url: http://localhost:8080/blacklab-server
  protocol: bls
```

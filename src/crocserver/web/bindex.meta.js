

orgMeta = {
    orgUrl: {
        label: "Organisation URL"
    },
    orgCode: {
        label: "Organisation code"
    },
    orgName: {
        label: "Organisation name"
    },
    region: {
        label: "Region (e.g. State or Province)"
    },
    locality: {
        label: "Locality (e.g. City)"
    },
    countryCode: {
        label: "Country (TLD code)"
    },
};

orgMeta.editOrg = [
    orgMeta.orgUrl,
    orgMeta.orgCode,
    orgMeta.orgName,
    orgMeta.region,
    orgMeta.locality,
    orgMeta.countryCode
];

orgMeta.listOrg = [
    orgMeta.orgUrl,
    orgMeta.orgCode,
    orgMeta.orgName,
    orgMeta.region,
    orgMeta.locality,
    orgMeta.countryCode
];

function initTestMeta() {
    console.log('initTestMeta');
    console.log(orgMeta);
    console.log(orgMeta.editOrg);
}

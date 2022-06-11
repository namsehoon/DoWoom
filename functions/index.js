const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp();

exports.everyDayTask = functions.pubsub.schedule("every day 23:59")
    .onRun((context) => {
      functions.database.ref("/GameCount/{uid}").onWrite((event) => {
        const ref = event.data.ref;
        ref.update({
          "count": 0,
        });
      });
      return true;
    });

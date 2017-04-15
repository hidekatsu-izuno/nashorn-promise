/**
 * nashorn-promise
 *
 * @author hidekatsu.izuno@gmail.com (Hidekatsu Izuno)
 * @license MIT License
 */

(function(global) {
	'use strict';

	var JCompletableFuture = Java.type('java.util.concurrent.CompletableFuture');

	var Promise = function Promise(resolver) {
		var that = this;
		if (resolver instanceof JCompletableFuture) {
			that._future = resolver;
		} else {
			that._future = JCompletableFuture.supplyAsync(function() {
				var result = {};
				try {
					(0, resolver)(function(value) {
						result.status = 'fulfilled';
						result.value = value;
					}, function(reason) {
						result.status = 'rejected';
						result.reason = reason;
					});
				} catch (e) {
					result.status = 'rejected';
					result.reason = e.message;
				}
				return result;
			});
		}
	};

	Promise.all = function(array) {
		var targets = array.map(function(elem) {
			if (elem instanceof Promise) {
				return elem._future;
			}
			return Promise.resolve(elem)._future;
		});
		return new Promise(JCompletableFuture.allOf(targets));
	};

	Promise.race = function(array) {
		var targets = array.map(function(elem) {
			if (elem instanceof Promise) {
				return elem._future;
			}
			return Promise.resolve(elem)._future;
		});
		return new Promise(JCompletableFuture.anyOf(targets));
	};

	Promise.resolve = function(value) {
		if (value instanceof Promise) {
			return value;
		} else if (value !== null && (typeof value === 'function' || typeof value === 'object') && value.then === 'function') {
			return new Promise(function(fulfill, reject) {
				var result = {};
				try {
					result.value = value.then(fulfill, reject);
					result.status = 'fulfilled';
				} catch (e) {
					result.value = e;
					result.status = 'rejected';
				}
				return result;
			});
		} else {
			return new Promise(JCompletableFuture.completeFutuer({
				status: 'fulfilled',
				value: value
			}));
		}
	};

	Promise.reject = function(value) {
		return new Promise(JCompletableFuture.completeFutuer({
			status: 'rejected',
			value: value
		}));
	};

	Promise.prototype.then = function(onFulfillment, onRejection) {
		var that = this;
		return new Promise(that._future.thenApply(function(result) {
			var result2 = {
				status: result.status,
				value: result.value,
				reason: result.reason
			};
			try {
				if (result.status == 'fulfilled') {
					if (typeof onFulfillment === 'function') {
						result2.value = (0, onFulfillment)(result.value);
						result2.reason = undefined;
					}
				} else if (result.status == 'rejected') {
					if (typeof onRejection === 'function') {
						result2.value = (0, onRejection)(result.reason);
						result2.reason = undefined;
						result2.status = 'fulfilled';
					}
				}
			} catch (e) {
				result2.status = 'rejected';
				result2.value = undefined;
				result2.reason = e;
			}
			return result2;
		}));
	};

	Promise.prototype.catch = function(onRejection) {
		return this.then(null, onRejection);
	};

	global.Promise = Promise;
})(Function('return this')());
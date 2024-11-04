function displayAlert(element, message, type) {
  element.textContent = message;
  element.classList.remove('success', 'error'); // 기존 상태 제거
  element.classList.add('message'); // message 기본 스타일 추가

  // type이 빈 문자열이 아닐 때만 추가
  if (type) {
    element.classList.add(type); // success 또는 error만 추가
  }
}

function handleSubscriptionRequest(event) {
  event.preventDefault();
  const email = document.getElementById('subscribeEmail').value;
  const messageElement = document.getElementById('subscriptionMessage');
  const submitButton = event.target.querySelector('button');

  // 버튼 비활성화
  submitButton.disabled = true;
  submitButton.classList.add('disabled');

  // 요청 직후 메시지 업데이트
  displayAlert(messageElement, '구독 요청 중입니다. 잠시만 기다려주세요...', '');

  // 서버에 비동기 요청 보내기
  fetch('/api/subscription/request', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({ email: email })
  })
      .then(response => response.text())
      .then(data => {
        if (data.includes('이미 구독 중')) {
          displayAlert(messageElement, data, 'error');
        } else {
          displayAlert(messageElement, '구독 요청이 성공적으로 완료되었습니다. 인증 번호를 입력해주세요.', 'success');
          document.getElementById('subscriptionTokenSection').classList.remove('hidden');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        displayAlert(messageElement, '구독 요청 중 오류가 발생했습니다.', 'error');
      })
      .finally(() => {
        // 요청 완료 후 버튼 다시 활성화
        submitButton.disabled = false;
        submitButton.classList.remove('disabled');
      });
}

function handleUnsubscriptionRequest(event) {
  event.preventDefault();
  const email = document.getElementById('unsubscribeEmail').value;
  const messageElement = document.getElementById('unsubscriptionMessage');
  const submitButton = event.target.querySelector('button');

  // 버튼 비활성화
  submitButton.disabled = true;
  submitButton.classList.add('disabled');

  // 요청 직후 메시지 업데이트
  displayAlert(messageElement, '구독 취소 요청 중입니다. 메일을 확인해주세요.', '');

  // 서버에 비동기 요청 보내기
  fetch('/api/subscription/cancel/request', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({ email: email })
  })
      .then(response => response.text())
      .then(data => {
        if (data.includes('[ERROR]')) {
          displayAlert(messageElement, data, 'error');
        } else {
          displayAlert(messageElement, '구독 취소 요청이 성공적으로 완료되었습니다. 인증 번호를 입력해주세요.', 'success');
          document.getElementById('unsubscriptionTokenSection').classList.remove('hidden');
        }
      })
      .catch(error => {
        console.error('Error:', error);
        displayAlert(messageElement, '구독 취소 요청 중 오류가 발생했습니다.', 'error');
      })
      .finally(() => {
        // 요청 완료 후 버튼 다시 활성화
        submitButton.disabled = false;
        submitButton.classList.remove('disabled');
      });
}

function handleVerifySubscription(event) {
  event.preventDefault();  // 폼 제출 방지
  const email = document.getElementById('subscribeEmail').value;
  const token = document.getElementById('subscribeToken').value;
  const messageElement = document.getElementById('subscriptionMessage');
  const submitButton = event.target.querySelector('button');

  // 버튼 비활성화
  submitButton.disabled = true;
  submitButton.classList.add('disabled');

  // 확인 요청 중 메시지 업데이트
  displayAlert(messageElement, '인증 번호 확인 중입니다...', '');

  // 서버에 비동기 요청 보내기
  fetch('/api/subscription', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({ email: email, token: token })
  })
      .then(response => response.text())
      .then(data => {
        displayAlert(messageElement, data, 'success');
      })
      .catch(error => {
        console.error('Error:', error);
        displayAlert(messageElement, '인증 번호 확인 중 오류가 발생했습니다.', 'error');
      })
      .finally(() => {
        // 요청 완료 후 버튼 다시 활성화
        submitButton.disabled = false;
        submitButton.classList.remove('disabled');
      });
}

function handleVerifyUnsubscription(event) {
  event.preventDefault();  // 폼 제출 방지
  const email = document.getElementById('unsubscribeEmail').value;
  const token = document.getElementById('unsubscribeToken').value;
  const messageElement = document.getElementById('unsubscriptionMessage');
  const submitButton = event.target.querySelector('button');

  // 버튼 비활성화
  submitButton.disabled = true;
  submitButton.classList.add('disabled');

  // 확인 요청 중 메시지 업데이트
  displayAlert(messageElement, '구독 취소 확인 중입니다...', '');

  // 서버에 비동기 요청 보내기
  fetch('/api/subscription/cancel', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: new URLSearchParams({ email: email, token: token })
  })
      .then(response => response.text())
      .then(data => {
        displayAlert(messageElement, data, 'success');
      })
      .catch(error => {
        console.error('Error:', error);
        displayAlert(messageElement, '구독 취소 확인 중 오류가 발생했습니다.', 'error');
      })
      .finally(() => {
        // 요청 완료 후 버튼 다시 활성화
        submitButton.disabled = false;
        submitButton.classList.remove('disabled');
      });
}
